/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duduto.chan.process;

import com.duduto.chan.enums.Command;
import com.duduto.chan.enums.Field;
import com.duduto.chan.enums.GameState;
import com.duduto.chan.model.GamePlayer;
import com.duduto.chan.model.Player;
import com.duduto.util.MessagingHelper;
import com.electrotank.electroserver5.extensions.api.PluginApi;
import com.electrotank.electroserver5.extensions.api.ScheduledCallback;
import com.electrotank.electroserver5.extensions.api.value.EsObject;

/**
 *
 * @author Blacker
 */
public class AutoPlayGame {

    PluginApi api;
    GamePlayer gamePlayer;
    Player player = null;

    public AutoPlayGame(GamePlayer game, Player player, PluginApi api) {
        gamePlayer = game;
        this.player = player;
        this.api = api;
    }

    public void disCard(final int cardId, final String command) {
        if (gamePlayer.getGameState() == GameState.Started) {
            api.scheduleExecution(gamePlayer.getTimeWaiting(), 1, new ScheduledCallback() {
                @Override
                public void scheduledCallback() {
                    EsObject es = new EsObject();
                    player.disCard(cardId);
                    int turn = gamePlayer.getNextTurn(player);
                    Player nextTurn = gamePlayer.getRight(player);
                    nextTurn.prevDisCard();
                    es.setString(Field.Command.getName(), command);
                    es.setInteger(Field.CardId.getName(), cardId);
                    es.setString(Field.UserName.getName(), player.getUsername());
                    es.setInteger(Field.Position.getName(), player.getPosition());
                    gamePlayer.setCurrentTurn(turn);
                    es.setInteger(Field.CurrentTurn.getName(), gamePlayer.getCurrentTurn());
                    MessagingHelper.sendMessageToRoom(es, api);
                    if (nextTurn.getPlayerData().isIsAuto()) {
                        draw(nextTurn, api);
                    }
                }
            });
        }
    }

    public void draw(final Player p, final PluginApi api) {
        if (gamePlayer.getGameState() == GameState.Started) {
            if (!gamePlayer.getNoc().isEmpty()) {
                api.scheduleExecution(gamePlayer.getTimeWaiting(), 1, new ScheduledCallback() {
                    @Override
                    public void scheduledCallback() {
                        api.getLogger().debug("===========draw============");
                        EsObject es = new EsObject();
                        es.setString(Field.Command.getName(), Command.Draw.getCommand());
                        if (gamePlayer.isCurrentTurn(p)) {
                            if (p.isDraw()) {
                                es.setString(Field.UserName.getName(), p.getUsername());
                                es.setInteger(Field.Position.getName(), p.getPosition());
                                int id = gamePlayer.draw(p);
                                es.setInteger(Field.CardId.getName(), id);
                                api.getLogger().debug("draw cardid " + id);
                                MessagingHelper.sendMessageToRoom(es, api);
                                p.afterDraw();
                                skip(p, id);
                            } else {
                                api.getLogger().debug("ko dc rut");
                            }
                        } else {
                            api.getLogger().debug("chua den luot");
                        }
                    }
                });
            }
        }
    }

    public void skip(final Player p, final int id) {
        if (gamePlayer.getGameState() == GameState.Started) {
            api.scheduleExecution(gamePlayer.getTimeWaiting(), 1, new ScheduledCallback() {
                @Override
                public void scheduledCallback() {
                    api.getLogger().debug("===========skip============");
                    EsObject es = new EsObject();
                    if (gamePlayer.isCurrentTurn(p)) {
                        if (p.isSkip()) {
                            es.setString(Field.UserName.getName(), p.getUsername());
                            p.afterSkip();
                            player = p;
                            disCard(id, Command.Skip.getCommand());
                        } else {
                            api.getLogger().debug("ko dc duoi");
                        }
                    } else {
                        api.getLogger().debug("chua den luot");
                    }
                }
            });
        }
    }

    public void autoSteal(final int cardId) {
        if (gamePlayer.getGameState() == GameState.Started) {
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
