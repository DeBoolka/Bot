package dikanev.nikita.bot.controller;

import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.objects.AmmunitionObject;
import dikanev.nikita.bot.logic.connector.core.AmmunitionCoreConnector;
import dikanev.nikita.bot.logic.connector.db.users.UserDBConnector;

import java.sql.SQLException;

public class AmmunitionController {
    public static AmmunitionObject addAmmunition(String token, int userId, String name, String[] photoLinks) throws ApiException, SQLException {
        return AmmunitionCoreConnector.addAmmunition(token, UserDBConnector.getIdCore(userId), name, photoLinks);
    }
}
