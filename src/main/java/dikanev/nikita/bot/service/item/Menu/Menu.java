package dikanev.nikita.bot.service.item.Menu;

import com.google.gson.JsonObject;
import dikanev.nikita.bot.controller.groups.AccessGroupController;
import dikanev.nikita.bot.logic.callback.CommandResponse;
import dikanev.nikita.bot.logic.callback.commands.VkCommand;
import dikanev.nikita.bot.service.storage.clients.CoreClientStorage;

import java.util.*;

public class Menu {

    private String startMessage = "Что хотите сделать?";
    private List<WayData> ways = new ArrayList<>();

    public Menu way(WayData wayData) {
        ways.add(wayData);
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
        new VkCommand.SendMessage(resp.getUserId()).message("Команда не найдена").saveExecute();
        return resp.finish();
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
        Map<String, String> helpMessages = new HashMap<>(ways.size());
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

        public WayData way(String name, Point.Work in, Point.Work payload) {
            this.way = new Way(new Point(name, in, payload));
            return this;
        }

        public WayData way(Point.Work in, Point.Work payload) {
            this.way = new Way(new Point("start", in, payload));
            return this;
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
