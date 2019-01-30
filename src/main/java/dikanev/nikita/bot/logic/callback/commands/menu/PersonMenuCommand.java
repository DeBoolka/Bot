package dikanev.nikita.bot.logic.callback.commands.menu;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import dikanev.nikita.bot.api.exceptions.InvalidParametersException;
import dikanev.nikita.bot.api.objects.JObject;
import dikanev.nikita.bot.api.objects.UserInfoObject;
import dikanev.nikita.bot.api.objects.UserObject;
import dikanev.nikita.bot.controller.users.UserController;
import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.VkCommands;
import dikanev.nikita.bot.logic.callback.commands.MenuCommand;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PersonMenuCommand extends MenuCommand {

    private static final Logger LOG = LoggerFactory.getLogger(PersonMenuCommand.class);

    private final static int COUNT_COLUMN_PUBLIC_DATA_OF_USER = 2;

    private final static String[] publicDataOfUser = new String[]{
            "Дата рождения", "age",
            "Телефон", "phone",
            "Город", "city",
            "Позывной в игре", "gameOnName"
    };

    @Override
    protected Map<String, CommandData> getCommands(CommandResponse cmdResp, Parameter args) {
        Map<String, CommandData> res = new LinkedHashMap<>();

        res.put("bot/vk/person/info/get", new CommandData("Информация", "- Выводит вашу основную информацию", true, (resp, data, commands) -> {
            args.set("message", getPersonInfo(cmdResp, args));
            return cmdResp.setInit();
        }));
        res.put("bot/vk/person/info/update", new CommandData("Изменить", "- Заполнение или изменение информации о себе", true, (resp, data, commands) -> {
            addWorker(args, "change-info", "change-info");
            new SendMessage(resp.getUserId()).message("Выберите цифру информации, которую хотите редактировать\n" + getNamesOfPublicDataOfUser()).execute();
            return cmdResp.finish();
        }));
        res.put("bot/vk/person/photo", new CommandData("Фото", "- Ваши фотографии", true, (resp, data, commands) ->
                cmdResp.setArgs("").setIdCommand(VkCommands.PHOTO_OF_USER.id()).setInit()
        ));
        res.put("bot/vk/person/ammunition", new CommandData("Снаряжение", "- Ваше снаряжение", true, (resp, data, commands) ->
                cmdResp.setArgs("").setIdCommand(VkCommands.AMMUNITION_OF_USER.id()).setInit()
        ));
        res.put("help", new CommandData("help", true, "- Выводит список команд", Keyboard.DEFAULT, (resp, data, commands) -> {
            args.set("message", helpCommand(commands));
            return cmdResp.setInit();
        }));
        res.put("menu", new CommandData("menu", true, "- Главное меню", Keyboard.DEFAULT, (resp, data, commands) ->
                cmdResp.setArgs("").setIdCommand(VkCommands.MENU.id()).setInit()
        ));

        return res;

    }

    private String getPersonInfo(CommandResponse cmdResp, Parameter args) throws ClientException, ApiException, SQLException, dikanev.nikita.bot.api.exceptions.ApiException {
        JObject userData = UserController.getPersonalDataOfUser(CoreClientStorage.getInstance().getToken()
                , cmdResp.getUserId()
                , "userId", "name", "s_name", "login", "email", "age", "phone", "city", "nameOnGame");
        if (userData == null) {
            return "Команда временно недоступна.";
        }

        UserObject user = userData.cast(UserObject.empty());
        UserInfoObject userInfo = userData.cast(UserInfoObject.empty());

        StringBuilder info = new StringBuilder("Информация\n");
        info.append("id: ").append(userInfo.userId > 0 ? userInfo.userId : "неизвестно").append("\n");
        info.append("Имя: ").append(user.getName() != null ? user.getName() : "неизвестно").append("\n");
        info.append("Фамилия: ").append(user.getS_name() != null ? user.getS_name() : "неизвестно").append("\n");
        info.append("Логин: ").append(userInfo.login != null ? userInfo.login : "неизвестно").append("\n");
        info.append("email: ").append(userInfo.email != null ? userInfo.email : "неизвестно").append("\n");
        info.append("Дата рождения: ").append(userInfo.age != null ? userInfo.age : "неизвестно").append("\n");
        info.append("Телефон: ").append(userInfo.phone != null ? userInfo.phone : "неизвестно").append("\n");
        info.append("Город: ").append(userInfo.city != null ? userInfo.city : "неизвестно").append("\n");
        info.append("Позывной в игре: ").append(userInfo.nameOnGame != null ? userInfo.nameOnGame : "неизвестно");

        return info.toString();
    }

    @Override
    protected String getHelloMessage(CommandResponse cmd) {
        return "Вы в вашем личном кабинете.";
    }

    @Override
    protected List<Worker> initWorkers(CommandResponse resp, Parameter param) {
        return new ArrayList<>(List.of(
                new Worker("change-info", it -> changeInfo(resp, resp.getText()))
        ));
    }

    private void changeInfo(CommandResponse resp, String text) {
        Parameter param = resp.getArgs();
        if (text == null) {
            return;
        }

        try {
            try {
                if (!param.contains("change-data")) {
                    int indexChangedDate = Integer.valueOf(text);
                    for (int i = 0; i < publicDataOfUser.length; i += COUNT_COLUMN_PUBLIC_DATA_OF_USER) {
                        if (i / COUNT_COLUMN_PUBLIC_DATA_OF_USER + 1 == indexChangedDate) {
                            param.set("change-data", publicDataOfUser[i + 1]);
                            new SendMessage(resp.getUserId()).message("Введите новые данные").execute();
                            return;
                        }
                    }
                    new SendMessage(resp.getUserId()).message("Не верный индекс").execute();
                    resp.setArgs("");
                    return;
                }

                if (text.trim().isEmpty()) {
                    return;
                }
                boolean isUpdate = UserController.updateUserInfo(CoreClientStorage.getInstance().getToken()
                        , resp.getUserId()
                        , param.getF("change-data")
                        , text.trim()
                );

                new SendMessage(resp.getUserId()).message(isUpdate ? "Информация успешно сохранена" : "Команда временно недоступна").execute();
                resp.setArgs("");
                return;
            } catch (NumberFormatException e) {
                new SendMessage(resp.getUserId()).message("Не корректный индекс.").execute();
            }
        } catch (InvalidParametersException e) {
            try {
                new SendMessage(resp.getUserId()).message("Не верный индекс или формат нового значения").execute();
            } catch (Exception ex) {
                LOG.error("Fatal error: ", ex);
            }
        } catch (Exception e) {
            LOG.error("Failed change info: ", e);
        }
        resp.setArgs("");
    }

    private static String getNamesOfPublicDataOfUser() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < publicDataOfUser.length; i += COUNT_COLUMN_PUBLIC_DATA_OF_USER) {
            builder.append((i / COUNT_COLUMN_PUBLIC_DATA_OF_USER + 1))
                    .append(". ")
                    .append(publicDataOfUser[i])
                    .append("\n");
        }

        return builder.toString();
    }
}
