package dikanev.nikita.bot.service.item.Menu;

import com.google.gson.JsonObject;
import dikanev.nikita.bot.controller.groups.AccessGroupController;
import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.commands.VkCommand;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Menu {
    private static final Logger LOG = LoggerFactory.getLogger(Menu.class);

    private String startMessage = "Что хотите сделать?";
    private WayData defaultWay = null;
    private List<WayData> ways = new ArrayList<>();

    public Menu way(WayData wayData) {
        ways.add(wayData);
        return this;
    }

    public Menu defaultWay(WayData way) {
        if (!ways.contains(way)) {
            ways.add(way);
        }
        defaultWay = way;
        return this;
    }

    public String getStartMessage() {
        return startMessage;
    }

    public void startMessage(String startMessage) {
        this.startMessage = startMessage;
    }

    public List<String> getRequiredAccessVerification() {
        List<String> requiredAccessVerification = new ArrayList<>();
        ways.forEach(it -> {
            if (it.loadAccess) {
                requiredAccessVerification.add(it.accessAddress);
            }
        });
        return requiredAccessVerification;
    }

    public List<WayData.Button> getButtons(Map<String, Boolean> accesses) {
        List<WayData.Button> buttons = new ArrayList<>();
        ways.forEach(it -> {
            if (it.loadAccess && !accesses.get(it.accessAddress)) {
                return;
            }
            buttons.add(it.button);
        });
        return buttons;
    }

    public CommandResponse enter(CommandResponse resp) throws Exception {
        Bag bag = this.getBag(resp);
        String text;
        if (bag.getCurrentWayName() != null) {
            text = bag.getCurrentWayName();
        } else {
            text = resp.getText();
            bag.setCurrentWayName(text);
        }

        for (WayData wayData : ways) {
            if (wayData.stringsToEnter.contains(text)) {
                return enterToWay(resp, wayData, bag);
            }
        }
        if (defaultWay != null) {
            return enterToWay(resp, defaultWay, bag);
        }

        new VkCommand.SendMessage(resp.getUserId()).message("Команда не найдена").saveExecute();
        bag.clear();
        return resp.setInit();
    }

    private CommandResponse enterToWay(CommandResponse resp, WayData wayData, Bag bag) throws Exception {
        if (wayData.loadAccess) {
            Map<String, Boolean> accesses = AccessGroupController.getAccessUser(CoreClientStorage.getInstance().getToken()
                    , resp.getUserId()
                    , List.of(wayData.accessAddress)
            );
            if (!accesses.get(wayData.accessAddress)) {
                new VkCommand.SendMessage(resp.getUserId()).message("Команда не найдена").saveExecute();
                bag.clear();
                return resp.finish();
            }
        }

        return wayData.getWay().enterToWay(resp, bag);
    }

    private Bag getBag(CommandResponse resp) {
        JsonObject jsState = resp.getState();
        if (jsState == null) {
            jsState = new JsonObject();
            resp.setState(jsState);
        }
        if (!jsState.has("menu")) {
            jsState.add("menu", new JsonObject());
        }
        return new Bag(jsState.getAsJsonObject("menu"));
    }

    public Map<String, String> getHelpMessages(Map<String, Boolean> accesses) {
        Map<String, String> helpMessages = new LinkedHashMap<>(ways.size());
        ways.forEach(it -> {
            Boolean access = accesses.get(it.accessAddress);
            if ((access == null || access) && it.helpMessage != null) {
                helpMessages.put(it.button.stringOfButton, it.helpMessage);
            }
        });
        return helpMessages;
    }

    public static class WayData{
        String accessAddress = null;
        Set<String> stringsToEnter = new HashSet<>();
        Button button = new Button();
        boolean loadAccess = true;
        String helpMessage = null;
        Way way;

        public Way getWay() {
            return way;
        }

        public WayData accessAddress(String accessAddress) {
            this.accessAddress = accessAddress;
            return this;
        }

        public WayData stringsToEnter(String... stringsToEnter) {
            this.stringsToEnter.addAll(Set.of(stringsToEnter));
            return this;
        }

        public WayData button(String stringOfButton, String typeButton) {
            button = new Button(stringOfButton, typeButton);
            stringsToEnter.add(stringOfButton);
            return this;
        }

        public WayData buttonText(String stringOfButton) {
            button.setText(stringOfButton);
            stringsToEnter.add(stringOfButton);
            return this;
        }

        public WayData buttonType(String typeButton) {
            button.setType(typeButton);
            return this;
        }

        public WayData loadAccess(boolean loadAccess) {
            this.loadAccess = loadAccess;
            return this;
        }

        //----------------------
        //Ways
        //----------------------

        public WayData point(Point point) {
            if (way == null) {
                this.way = new Way();
            }
            this.way.putPoint(point);
            return this;
        }

        public WayData point(String name, Point.Work in, Point.Work payload) {
            return point(new Point(name, in, payload));
        }

        public WayData point(Point.Work in, Point.Work payload) {
            return point("start", in, payload);
        }

        public WayData point(String name, String inMessage, VkCommand.Keyboard keyboard, Point.Work payload) {
            return point(new Point(name, getSendMessageWorker(inMessage, keyboard), payload));
        }

        public WayData point(String name, String inMessage, Point.Work payload) {
            return point(name, inMessage, null, payload);
        }

        public WayData point(String inMessage, VkCommand.Keyboard keyboard, Point.Work payload) {
            return point("start", inMessage, keyboard, payload);
        }

        public WayData point(String inMessage, Point.Work payload) {
            return point("start", inMessage, null, payload);
        }

        //----------------------

        private Point.Work getSendMessageWorker(String inMessage, VkCommand.Keyboard keyboard) {
            return (resp, param, bag) -> {
                VkCommand.SendMessage sendMessage = new VkCommand.SendMessage(resp.getUserId()).message(inMessage);
                if (keyboard != null) {
                    sendMessage.button(keyboard);
                }
                sendMessage.execute();
                return null;
            };
        }

        public WayData help(String helpMessage) {
            this.helpMessage = helpMessage;
            return this;
        }

        public static class Button{
            public String stringOfButton = "";
            public String typeButton = VkCommand.Keyboard.DEFAULT;

            public Button(){}

            public Button(String stringOfButton, String typeButton) {
                this.stringOfButton = stringOfButton;
                this.typeButton = typeButton;
            }

            public void setText(String stringOfButton) {
                this.stringOfButton = stringOfButton;
            }

            public void setType(String typeButton) {
                this.typeButton = typeButton;
            }
        }
    }
}
