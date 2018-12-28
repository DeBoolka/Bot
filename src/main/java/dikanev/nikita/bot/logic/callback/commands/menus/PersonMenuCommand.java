package dikanev.nikita.bot.logic.callback.commands.menus;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import dikanev.nikita.bot.api.objects.JObject;
import dikanev.nikita.bot.api.objects.UserInfoObject;
import dikanev.nikita.bot.api.objects.UserObject;
import dikanev.nikita.bot.controller.users.UserController;
import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.VkCommands;
import dikanev.nikita.bot.logic.callback.commands.MenuCommand;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;
import org.apache.commons.collections4.map.LinkedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;

public class PersonMenuCommand extends MenuCommand {

    private static final Logger LOG = LoggerFactory.getLogger(PersonMenuCommand.class);

    @Override
    protected Map<String, CommandData> getCommands(CommandResponse cmdResp, Parameter args) {
        return new LinkedMap<>(Map.of(
                "menu", new CommandData("menu", true, "- Главное меню", (resp, data, commands) ->
                        cmdResp.setArgs("").setIdCommand(VkCommands.MENU.id()).setInit()
                ),"bot/vk/person/info", new CommandData("Информация", "- Выводит вашу основную информацию", true, (resp, data, commands) -> {
                    args.set("message", getPersonInfo(cmdResp, args));
                    return cmdResp.setInit();
                }),"help", new CommandData("help", true, "- Выводит список команд", (resp, data, commands) -> {
                    args.set("message", helpCommand(commands));
                    return cmdResp.setInit();
                })
        ));

    }

    private String getPersonInfo(CommandResponse cmdResp, Parameter args) throws ClientException, ApiException, SQLException, dikanev.nikita.bot.api.exceptions.ApiException {
        JObject userData = UserController.getPersonalDataOfUser(CoreClientStorage.getInstance().getToken()
                , cmdResp.getIdUser()
                , "userId", "name", "s_name", "login", "email", "age", "phone", "city", "nameOnGame");
        if (userData == null) {
            return "Команда временно недоступна.";
        }

        UserObject user = userData.cast(UserObject.empty());
        UserInfoObject userInfo = userData.cast(UserInfoObject.empty());

        StringBuilder info = new StringBuilder("Информация\n");
        info.append("id: ").append(userInfo.userId > 0 ? userInfo.userId : "неизвестно").append("\n");
        info.append("Имя: ").append(user.getName() != null ? user.getName() : "неизвестно").append("\n");
        info.append("Фамилия: ").append(user.getsName() != null ? user.getsName() : "неизвестно").append("\n");
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
}
