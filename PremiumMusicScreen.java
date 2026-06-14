package com.example;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

public class PremiumMusicScreen extends Screen {
    private int selectedTab = 0; 
    private final List<TrackData> playlist = new ArrayList<>();
    private int currentTrackIndex = 0;
    private boolean isPlaying = false;

    public PremiumMusicScreen() {
        super(Text.literal("Premium Music Player"));
        playlist.add(new TrackData("Neon Drive", "Synthwave Master", "assets/musicmod/songs/neon_drive.wav"));
        playlist.add(new TrackData("Cyberpunk Horizon", "Lofi Beats", "assets/musicmod/songs/cyberpunk.wav"));
        playlist.add(new TrackData("Minecraft Theme (Remix)", "C418", "assets/musicmod/songs/c418_remix.wav"));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        fill(matrices, 10, 10, 90, this.height - 20, 0x26FFFFFF); 
        fill(matrices, 100, 10, this.width - 10, this.height - 20, 0x1AFFFFFF);

        int textX = 20;
        if (selectedTab == 0) fill(matrices, 12, 45, 88, 65, 0x4D00FF88);
        this.client.textRenderer.draw(matrices, "🏠 Home", textX, 50, selectedTab == 0 ? 0x00FF88 : 0xFFFFFF);

        if (selectedTab == 1) fill(matrices, 12, 75, 88, 95, 0x4D00FF88);
        this.client.textRenderer.draw(matrices, "🎨 Themes", textX, 80, selectedTab == 1 ? 0x00FF88 : 0xFFFFFF);

        if (selectedTab == 2) fill(matrices, 12, 105, 88, 125, 0x4D00FF88);
        this.client.textRenderer.draw(matrices, "⚙ FX EQ", textX, 110, selectedTab == 2 ? 0x00FF88 : 0xFFFFFF);

        int dockY = this.height - 50;
        fill(matrices, 105, dockY, this.width - 15, this.height - 15, 0x33000000);
        fill(matrices, 105, dockY, this.width - 15, this.height - 15, 0x26FFFFFF);
        
        TrackData current = playlist.get(currentTrackIndex);
        this.client.textRenderer.draw(matrices, "🎵 " + current.title, 115, dockY + 10, 0x00FF88);
        this.client.textRenderer.draw(matrices, current.artist, 115, dockY + 22, 0xAAAAAA);

        int btnX = this.width - 120;
        this.client.textRenderer.draw(matrices, "⏮", btnX, dockY + 15, 0xFFFFFF);
        this.client.textRenderer.draw(matrices, isPlaying ? "⏸" : "▶", btnX + 30, dockY + 15, 0x00FF88);
        this.client.textRenderer.draw(matrices, "⏭", btnX + 60, dockY + 15, 0xFFFFFF);

        if (selectedTab == 0) {
            this.client.textRenderer.draw(matrices, "🏠 PREMIUM MUSIC PLAYER", 115, 25, 0x00FF88);
            for (int i = 0; i < playlist.size(); i++) {
                int itemY = 55 + (i * 25);
                if (i == currentTrackIndex) {
                    fill(matrices, 110, itemY - 4, this.width - 20, itemY + 16, 0x3300FF88);
                } else {
                    fill(matrices, 110, itemY - 4, this.width - 20, itemY + 16, 0x0DFFFFFF);
                }
                this.client.textRenderer.draw(matrices, (i + 1) + ". " + playlist.get(i).title + " - " + playlist.get(i).artist, 120, itemY, i == currentTrackIndex ? 0x00FF88 : 0xFFFFFF);
            }
        } 
        else if (selectedTab == 1) {
            this.client.textRenderer.draw(matrices, "🎨 INTERACTIVE THEMES", 115, 25, 0x00FF88);
            this.client.textRenderer.draw(matrices, "• RGB Neon Modu (Aktif)", 115, 60, 0xFFFFFF);
        } 
        else if (selectedTab == 2) {
            this.client.textRenderer.draw(matrices, "⚙ FX EQUALIZER (10-BAND DSP)", 115, 25, 0x00FF88);
            AudioPlayerManager apm = AudioPlayerManager.getInstance();
            int startX = 130;
            int spacing = (this.width - startX - 40) / 10;
            int centerY = this.height / 2;

            for (int i = 0; i < 10; i++) {
                int currentX = startX + (i * spacing);
                int bandValue = apm.getEqualizerBand(i);
                fill(matrices, currentX + 4, centerY - 50, currentX + 6, centerY + 50, 0x33FFFFFF);
                int sliderY = centerY - bandValue;
                fill(matrices, currentX, sliderY - 4, currentX + 10, sliderY + 4, 0xFF00FF88);
                this.client.textRenderer.draw(matrices, (i + 1) + "B", currentX - 2, centerY + 60, 0xAAAAAA);
            }
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX >= 10 && mouseX <= 90) {
            if (mouseY >= 45 && mouseY <= 65) { selectedTab = 0; playClickSound(); return true; }
            if (mouseY >= 75 && mouseY <= 95) { selectedTab = 1; playClickSound(); return true; }
            if (mouseY >= 105 && mouseY <= 125) { selectedTab = 2; playClickSound(); return true; }
        }

        int dockY = this.height - 50;
        int btnX = this.width - 120;
        if (mouseY >= dockY + 5 && mouseY <= dockY + 35) {
            if (mouseX >= btnX && mouseX <= btnX + 20) {
                currentTrackIndex = (currentTrackIndex - 1 + playlist.size()) % playlist.size();
                if (isPlaying) playCurrentTrack();
                playClickSound();
                return true;
            }
            if (mouseX >= btnX + 30 && mouseX <= btnX + 50) {
                isPlaying = !isPlaying;
                if (isPlaying) playCurrentTrack();
                else AudioPlayerManager.getInstance().stopTrack();
                playClickSound();
                return true;
            }
            if (mouseX >= btnX + 60 && mouseX <= btnX + 80) {
                currentTrackIndex = (currentTrackIndex + 1) % playlist.size();
                if (isPlaying) playCurrentTrack();
                playClickSound();
                return true;
            }
        }

        if (selectedTab == 0 && mouseX >= 110 && mouseX <= this.width - 20) {
            for (int i = 0; i < playlist.size(); i++) {
                int itemY = 55 + (i * 25);
                if (mouseY >= itemY - 4 && mouseY <= itemY + 16) {
                    currentTrackIndex = i;
                    isPlaying = true;
                    playCurrentTrack();
                    playClickSound();
                    return true;
                }
            }
        }

        if (selectedTab == 2) handleEqualizerInput(mouseX, mouseY);
        return super.mouseClicked(matrices, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (selectedTab == 2) handleEqualizerInput(mouseX, mouseY);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private void handleEqualizerInput(double mouseX, double mouseY) {
        int startX = 130;
        int spacing = (this.width - startX - 40) / 10;
        int centerY = this.height / 2;

        if (mouseY >= centerY - 60 && mouseY <= centerY + 60) {
            for (int i = 0; i < 10; i++) {
                int currentX = startX + (i * spacing);
                if (mouseX >= currentX - 5 && mouseX <= currentX + 15) {
                    int newValue = centerY - (int) mouseY;
                    if (newValue > 50) newValue = 50;
                    if (newValue < -50) newValue = -50;
                    AudioPlayerManager.getInstance().setEqualizerBand(i, newValue);
                    break;
                }
            }
        }
    }

    private void playCurrentTrack() {
        AudioPlayerManager.getInstance().playTrack(playlist.get(currentTrackIndex).source);
    }

    private void playClickSound() {
        if (this.client != null && this.client.soundManager != null) {
            this.client.getSoundManager().play(
                net.minecraft.client.sound.PositionedSoundInstance.master(
                    net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK, 1.0F
                )
            );
        }
    }

    @Override
    public boolean shouldCloseOnEsc() { return true; }

    private static class TrackData {
        final String title;
        final String artist;
        final String source;

        TrackData(String title, String artist, String source) {
            this.title = title;
            this.artist = artist;
            this.source = source;
        }
    }
                                              }
