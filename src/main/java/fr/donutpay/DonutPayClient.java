package fr.donutpay;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DonutPayClient implements ClientModInitializer {

    // Regex qui détecte le message de paiement dans le chat.
    // Groupe 1 = pseudo, groupe 2 = montant. Adapte-la si le message du serveur est différent.
    private static final Pattern PAY = Pattern.compile(
            "(?i)(\\w{3,16}) (?:has )?(?:paid|sent) you \\$?([0-9][0-9.,]*[kmbt]?)");

    private static final long DURATION_MS = 5000; // durée d'affichage
    private static final long ANIM_MS = 300;      // glissement entrée / sortie

    private static volatile String from = "";
    private static volatile String amount = "";
    private static volatile long start = 0;

    @Override
    public void onInitializeClient() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> handle(message.getString()));
        ClientReceiveMessageEvents.CHAT.register((message, signed, sender, params, ts) -> handle(message.getString()));
        HudElementRegistry.addLast(Identifier.of("donutpay", "notif"), DonutPayClient::render);
    }

    private static void handle(String text) {
        Matcher m = PAY.matcher(text);
        if (m.find()) {
            from = m.group(1);
            amount = "$" + m.group(2).toUpperCase();
            start = System.currentTimeMillis();
        }
    }

    private static void render(DrawContext ctx, net.minecraft.client.render.RenderTickCounter tickCounter) {
        if (start == 0) return;
        long el = System.currentTimeMillis() - start;
        if (el >= DURATION_MS) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden) return;

        double p;
        if (el < ANIM_MS) p = el / (double) ANIM_MS;
        else if (el > DURATION_MS - ANIM_MS) p = (DURATION_MS - el) / (double) ANIM_MS;
        else p = 1.0;
        p = 1 - Math.pow(1 - p, 3); // easing

        int w = 150, h = 34;
        int x = ctx.getScaledWindowWidth() - (int) ((w + 6) * p);
        int y = 40;

        ctx.fill(x, y, x + w, y + h, 0xD9141414);
        ctx.fill(x, y, x + w, y + 1, 0x40FFFFFF);
        ctx.fill(x, y + h - 1, x + w, y + h, 0x40FFFFFF);
        ctx.fill(x, y, x + 1, y + h, 0x40FFFFFF);
        ctx.fill(x + w - 1, y, x + w, y + h, 0x40FFFFFF);

        // tête du joueur (si il est dans la liste des joueurs, sinon carré neutre)
        PlayerListEntry entry = mc.getNetworkHandler() != null
                ? mc.getNetworkHandler().getPlayerListEntry(from) : null;
        if (entry != null) {
            PlayerSkinDrawer.draw(ctx, entry.getSkinTextures(), x + 6, y + 6, 22);
        } else {
            ctx.fill(x + 6, y + 6, x + 28, y + 28, 0xFFC48A5A);
        }

        ctx.drawText(mc.textRenderer, from, x + 34, y + 6, 0xFFFFFFFF, true);
        ctx.drawText(mc.textRenderer, "t'a payé " + amount, x + 34, y + 18, 0xFF4CD964, true);

        // barre de timer
        int bw = (int) (w * (1.0 - (double) el / DURATION_MS));
        ctx.fill(x + 1, y + h - 3, x + 1 + Math.max(0, bw - 2), y + h - 1, 0xFF4CD964);
    }
}
