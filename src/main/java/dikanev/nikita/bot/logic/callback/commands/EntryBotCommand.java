package dikanev.nikita.bot.logic.callback.commands;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import dikanev.nikita.bot.api.groups.Groups;
import dikanev.nikita.bot.api.objects.UserObject;
import dikanev.nikita.bot.controller.users.UserController;
import dikanev.nikita.bot.logic.connector.core.UserCoreConnector;
import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.VkCommands;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

//Узел который приветствует юзера, и обрабатывает первичные данные
public class EntryBotCommand extends VkCommand {

    private static final Logger LOG = LoggerFactory.getLogger(EntryBotCommand.class);

    private static Pattern pattern = Pattern.compile("^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$", Pattern.CASE_INSENSITIVE);

    @Override
    public CommandResponse init(CommandResponse cmdResp, Parameter args) throws Exception {
        List<String> layers = getLayers(args);

        return welcomeMessageInit(cmdResp, args, layers);
    }

    @Override
    public CommandResponse handle(CommandResponse cmdResp, Parameter args) throws Exception {
        List<String> layers = getLayers(args);

        return welcomeMessageHandel(cmdResp, args, layers);
    }

    private List<String> getLayers(Parameter args) {
        if (!args.contains("layers") || !args.getFOrDefault("layersCmd", "").equals("entryBot")) {
            args.set("layers", "");
            args.set("layersCmd", "entryBot");
        }

        return args.get("layers");
    }

    private CommandResponse welcomeMessageInit(CommandResponse cmdResp, Parameter args, List<String> layers) throws ClientException, ApiException {
        if (!layers.contains("0")) {

            sendMessage("Привет.\n" +
                            "Для входа в систему мне надо узнать тебя лучше.\n" +
                            "Ты в любой момент можешь ввести команду 'back', чтобы вернуться к прошлому варианту.\n" +
                            "Для начала давай знакомиться.",
                    cmdResp.getIdUser());

            args.add("layers", "0");
            layers.add("0");
        }

        return setNameInit(cmdResp, args, layers);
    }

    private CommandResponse welcomeMessageHandel(CommandResponse cmdResp, Parameter args, List<String> layers) throws ClientException, ApiException {
        if (!layers.contains("0")) {
            return welcomeMessageInit(cmdResp, args, layers);
        }

        return setNameHandel(cmdResp, args, layers);
    }

    private CommandResponse setNameInit(CommandResponse cmdResp, Parameter args, List<String> layers) throws ClientException, ApiException {
        if (!layers.contains("1")) {
            sendMessage("Как тебя зовут?", cmdResp.getIdUser());
            return cmdResp.finish();
        }

        return setSNameInit(cmdResp, args, layers);
    }


    private CommandResponse setNameHandel(CommandResponse cmdResp, Parameter args, List<String> layers) throws ClientException, ApiException {
        if (!layers.contains("1")) {
            String name = cmdResp.getText();
            if (name == null || name.trim().isEmpty()) {
                sendMessage("Недопустимое имя, введите еще раз.", cmdResp.getIdUser());
                return cmdResp.finish();
            }

            sendMessage("Очень приятно " + name + "!", cmdResp.getIdUser());
            args.add("layers", "1").set("name", name);
            return cmdResp.setInit();
        }

        return setSNameHandel(cmdResp, args, layers);
    }

    private CommandResponse setSNameInit(CommandResponse cmdResp, Parameter args, List<String> layers) throws ClientException, ApiException {
        if (!layers.contains("2")) {
            List<List<TK>> btn = List.of(List.of(TK.getNegative("back")));

            sendMessage("Введи свою фамилию.", cmdResp.getIdUser(), btn);
            return cmdResp.finish();
        }

        return setEmailInit(cmdResp, args, layers);
    }

    private CommandResponse setSNameHandel(CommandResponse cmdResp, Parameter args, List<String> layers) throws ClientException, ApiException {
        if (!layers.contains("2")) {
            if (cmdResp.getText().equals("back")) {
                args.remove("layers", "1");
                return cmdResp.setInit();
            }

            String sname = cmdResp.getText();
            if (sname == null || sname.trim().isEmpty()) {
                sendMessage("Недопустимая фамилия, введите еще раз.", cmdResp.getIdUser());
                return cmdResp.finish();
            }

            args.add("layers", "2").set("s_name", sname);
            return cmdResp.setInit();
        }

        return setEmailHandel(cmdResp, args, layers);
    }

    private CommandResponse setEmailInit(CommandResponse cmdResp, Parameter args, List<String> layers) throws ClientException, ApiException {
        if (!layers.contains("3")) {
            List<List<TK>> btn = List.of(List.of(TK.getNegative("back")));

            sendMessage("Введи email.", cmdResp.getIdUser(), btn);
            return cmdResp.finish();
        }

        return setLoginInit(cmdResp, args, layers);
    }

    private CommandResponse setEmailHandel(CommandResponse cmdResp, Parameter args, List<String> layers) throws ClientException, ApiException {
        if (!layers.contains("3")) {
            if (cmdResp.getText().equals("back")) {
                args.remove("layers", "2");
                return cmdResp.setInit();
            }

            String email = cmdResp.getText();
            if (email == null
                    || email.trim().isEmpty()
                    || !pattern.matcher(email).matches() ) {
                sendMessage("Некорректный email.", cmdResp.getIdUser());
                return cmdResp.finish();
            } else if (UserCoreConnector.hasEmail(CoreClientStorage.getInstance().getToken(), email)) {
                sendMessage("Этот email уже используется.", cmdResp.getIdUser());
                return cmdResp.finish();
            }

            args.add("layers", "3").set("email", email);
            return cmdResp.setInit();
        }

        return setLoginHandel(cmdResp, args, layers);
    }

    private CommandResponse setLoginInit(CommandResponse cmdResp, Parameter args, List<String> layers) throws ClientException, ApiException {
        if (!layers.contains("4")) {
            List<List<TK>> btn = List.of(List.of(TK.getNegative("back")));

            sendMessage("Придумай логин.", cmdResp.getIdUser(), btn);
            return cmdResp.finish();
        }

        return registerUser(cmdResp, args, layers);
    }

    private CommandResponse setLoginHandel(CommandResponse cmdResp, Parameter args, List<String> layers) throws ClientException, ApiException {
        if (!layers.contains("4")) {
            if (cmdResp.getText().equals("back")) {
                args.remove("layers", "3");
                return cmdResp.setInit();
            }

            String login = cmdResp.getText();
            if (login == null
                    || login.trim().isEmpty()) {
                sendMessage("Некорректный логин", cmdResp.getIdUser());
                return cmdResp.finish();
            } else if (UserCoreConnector.hasLogin(CoreClientStorage.getInstance().getToken(), login)) {
                sendMessage("Этот логин занят.", cmdResp.getIdUser());
                return cmdResp.finish();
            }

            args.add("layers", "4").set("login", login);
            return cmdResp.setInit();
        }

        return setEmailHandel(cmdResp, args, layers);
    }

    private CommandResponse registerUser(CommandResponse cmdResp, Parameter args, List<String> layers) throws ClientException, ApiException {
        try {
            UserObject userCore = UserController.register(CoreClientStorage.getInstance().getToken()
                    , cmdResp.getIdUser()
                    , args.getF("name")
                    , args.getF("s_name")
                    , args.getF("email")
                    , args.getF("login")
                    , Groups.DEFAULT_GROUP);
        } catch (Exception e) {
            LOG.warn("Error reg: ", e);
            sendMessage("Что-то пошло не так при регистрации.\n" +
                    "Error: " + e.getMessage(), cmdResp.getIdUser());

            return cmdResp.setArgs("").setInit();
        }

        return cmdResp.setIdCommand(VkCommands.MENU.id()).setArgs("").setInit();
    }
}
