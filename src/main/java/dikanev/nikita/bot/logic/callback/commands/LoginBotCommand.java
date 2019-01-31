package dikanev.nikita.bot.logic.callback.commands;

import dikanev.nikita.bot.api.groups.Groups;
import dikanev.nikita.bot.api.item.Email;
import dikanev.nikita.bot.api.item.Login;
import dikanev.nikita.bot.api.objects.UserObject;
import dikanev.nikita.bot.controller.users.UserController;
import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.VkCommands;
import dikanev.nikita.bot.logic.connector.core.UserCoreConnector;
import dikanev.nikita.bot.service.client.parameter.Parameter;
import dikanev.nikita.bot.service.item.Menu.*;
import dikanev.nikita.bot.service.item.Menu.Menu.*;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginBotCommand extends WayMenuCommand {
    private static final Logger LOG = LoggerFactory.getLogger(LoginBotCommand.class);

    private final static String BACK_MESSAGE = "Назад";
    private final static Keyboard BACK_BUTTON = new Keyboard().def(BACK_MESSAGE);

    static {
        setMenu(new Menu())
                .defaultWay(getLoginWay());
    }

    private static WayData getLoginWay() {
        return new WayData()
                .loadAccess(false)
                .point("Привет.\n" +
                                "Для входа в систему мне надо узнать тебя лучше.\n" +
                                "Ты в любой момент можешь ввести команду 'Назад', чтобы вернуться к прошлому варианту.\n" +
                                "Для начала давай знакомиться.\n" +
                                "Как тебя зовут?",
                        (resp, param, bag) -> {
                            String name = resp.getText();
                            if (name == null || name.trim().isEmpty()) {
                                new SendMessage(resp.getUserId()).message("Недопустимое имя, введите еще раз.").execute();
                                return null;
                            }

                            new SendMessage(resp.getUserId()).message("Очень приятно " + name + "!").execute();
                            param.add("name", name);
                            return bag.getWay().getPointByName("s_name");
                        }
                ).point("s_name", "Введите свою фамилию", BACK_BUTTON, getSNamePayload())
                .point("email", "Введите свой email", BACK_BUTTON, getEmailPayload())
                .point("login", "Придумайте логин", BACK_BUTTON, getLoginPayload())
                .point("register", getRegisterIn(), null);
    }

    private static Point.Work getSNamePayload() {
        return (resp, param, bag) -> {
            String sname = resp.getText();
            if (sname.toLowerCase().equals(BACK_MESSAGE.toLowerCase())) {
                param.remove("name");
                return bag.setBeen("start", false)
                        .setBeen(bag.getCurrentPointName(), false).getWay().getPointByName("start");
            }

            if (sname.trim().isEmpty() || sname.length() == 1) {
                new SendMessage(resp.getUserId()).message("Недопустимая фамилия, введите еще раз.").button(BACK_BUTTON).execute();
                return null;
            }

            param.add("s_name", sname);
            return bag.getWay().getPointByName("email");
        };
    }

    private static Point.Work getEmailPayload() {
        return (resp, param, bag) -> {
            String emailText = resp.getText();
            if (emailText.toLowerCase().equals(BACK_MESSAGE.toLowerCase())) {
                return moveToPoint("s_name", resp, param, bag);
            }

            Email email = new Email(emailText);
            if (!email.isValid()) {
                new SendMessage(resp.getUserId()).message("Некорректный email.").button(BACK_BUTTON).execute();
                return null;
            } else if (UserCoreConnector.hasEmail(CoreClientStorage.getInstance().getToken(), email.toString())) {
                new SendMessage(resp.getUserId()).message("Этот email уже используется.").button(BACK_BUTTON).execute();
                return null;
            }

            param.add("email", email.toString());
            return bag.getWay().getPointByName("login");
        };
    }

    private static Point.Work getLoginPayload() {
        return (resp, param, bag) -> {
            String loginText = resp.getText();
            if (loginText.toLowerCase().equals(BACK_MESSAGE.toLowerCase())) {
                return moveToPoint("email", resp, param, bag);
            }

            Login login = new Login(loginText);
            if (!login.isValid()) {
                new SendMessage(resp.getUserId()).message("Некорректный логин").execute();
                return null;
            } else if (UserCoreConnector.hasLogin(CoreClientStorage.getInstance().getToken(), login.getLogin())) {
                new SendMessage(resp.getUserId()).message("Этот логин занят.").execute();
                return null;
            }

            param.add("login", login.getLogin());
            return bag.getWay().getPointByName("register");
        };
    }

    private static Point moveToPoint(String pointName, CommandResponse resp, Parameter param, Bag bag) {
        param.remove(pointName);
        return bag.setBeen(pointName, false)
                .setBeen(bag.getCurrentPointName(), false).getWay().getPointByName(pointName);
    }

    private static Point.Work getRegisterIn() {
        return (resp, param, bag) -> {
            try {
                UserObject userCore = UserController.register(CoreClientStorage.getInstance().getToken()
                        , resp.getUserId()
                        , param.getF("name")
                        , param.getF("s_name")
                        , param.getF("email")
                        , param.getF("login")
                        , Groups.DEFAULT_GROUP);
            } catch (Exception e) {
                LOG.error("Failed register: ", e);
                new SendMessage(resp.getUserId()).message("Что-то пошло не так при регистрации.\n" +
                        "Error: " + e.getMessage()).execute();

                resp.setArgs("").setHandle();
                return exitWay(resp);
            }

            resp.setIdCommand(VkCommands.MENU.id()).setArgs("").setInit();
            return exitWay(resp);
        };
    }
}
