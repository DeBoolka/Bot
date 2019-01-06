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

            new SendMessage(cmdResp.getIdUser()).message("Привет.\n" +
                    "Для входа в систему мне надо узнать тебя лучше.\n" +
                    "Ты в любой момент можешь ввести команду 'back', чтобы вернуться к прошлому варианту.\n" +
                    "Для начала давай знакомиться.").execute();

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
            new SendMessage(cmdResp.getIdUser()).message("Как тебя зовут?").execute();
            return cmdResp.finish();
        }

        return setSNameInit(cmdResp, args, layers);
    }


    private CommandResponse setNameHandel(CommandResponse cmdResp, Parameter args, List<String> layers) throws ClientException, ApiException {
        if (!layers.contains("1")) {
            String name = cmdResp.getText();
            if (name == null || name.trim().isEmpty()) {
                new SendMessage(cmdResp.getIdUser()).message("Недопустимое имя, введите еще раз.").execute();
                return cmdResp.finish();
            }

            new SendMessage(cmdResp.getIdUser()).message("Очень приятно " + name + "!").execute();
            args.add("layers", "1").set("name", name);
            return cmdResp.setInit();
        }

        return setSNameHandel(cmdResp, args, layers);
    }

    private CommandResponse setSNameInit(CommandResponse cmdResp, Parameter args, List<String> layers) throws ClientException, ApiException {
        if (!layers.contains("2")) {
            new SendMessage(cmdResp.getIdUser()).message("Введи свою фамилию.").button(new Keyboard().def("back")).execute();
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
                new SendMessage(cmdResp.getIdUser()).message("Недопустимая фамилия, введите еще раз.").execute();
                return cmdResp.finish();
            }

            args.add("layers", "2").set("s_name", sname);
            return cmdResp.setInit();
        }

        return setEmailHandel(cmdResp, args, layers);
    }

    private CommandResponse setEmailInit(CommandResponse cmdResp, Parameter args, List<String> layers) throws ClientException, ApiException {
        if (!layers.contains("3")) {
            new SendMessage(cmdResp.getIdUser()).message("Введи email.").button(new Keyboard().def("back")).execute();
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
                new SendMessage(cmdResp.getIdUser()).message("Некорректный email.").execute();
                return cmdResp.finish();
            } else if (UserCoreConnector.hasEmail(CoreClientStorage.getInstance().getToken(), email)) {
                new SendMessage(cmdResp.getIdUser()).message("Этот email уже используется.").execute();
                return cmdResp.finish();
            }

            args.add("layers", "3").set("email", email);
            return cmdResp.setInit();
        }

        return setLoginHandel(cmdResp, args, layers);
    }

    private CommandResponse setLoginInit(CommandResponse cmdResp, Parameter args, List<String> layers) throws ClientException, ApiException {
        if (!layers.contains("4")) {
            new SendMessage(cmdResp.getIdUser()).message("Придумай логин.").button(new Keyboard().def("back")).execute();
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
                new SendMessage(cmdResp.getIdUser()).message("Некорректный логин").execute();
                return cmdResp.finish();
            } else if (UserCoreConnector.hasLogin(CoreClientStorage.getInstance().getToken(), login)) {
                new SendMessage(cmdResp.getIdUser()).message("Этот логин занят.").execute();
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
            new SendMessage(cmdResp.getIdUser()).message("Что-то пошло не так при регистрации.\n" +
                    "Error: " + e.getMessage()).execute();

            return cmdResp.setArgs("").setInit();
        }

        return cmdResp.setIdCommand(VkCommands.MENU.id()).setArgs("").setInit();
    }
}
