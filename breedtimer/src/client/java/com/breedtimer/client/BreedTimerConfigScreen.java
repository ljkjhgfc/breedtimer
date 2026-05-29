package com.breedtimer.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class BreedTimerConfigScreen extends Screen {

    private final Screen parent;

    public BreedTimerConfigScreen(Screen parent) {
        super(Text.literal("Breed Timer Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 2 - 60;

        // Scale slider
        this.addDrawableChild(new ScaleSlider(centerX - 100, startY, 200, 20,
                Text.literal("Timer Scale: "), BreedTimerConfig.scale));

        // Reset positions button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Reset All Timer Positions"),
                btn -> {
                    BreedTimerConfig.mobPositions.clear();
                    BreedTimerConfig.save();
                }
        ).dimensions(centerX - 100, startY + 30, 200, 20).build());

        // Info label button (non-interactive)
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Drag timers in-game to reposition"),
                btn -> {}
        ).dimensions(centerX - 100, startY + 60, 200, 20).build());

        // Done button
        this.addDrawableChild(ButtonWidget.builder(
                ScreenTexts.DONE,
                btn -> close()
        ).dimensions(centerX - 100, startY + 100, 200, 20).build());
    }

    @Override
    public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title,
                this.width / 2, this.height / 2 - 90, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        BreedTimerConfig.save();
        this.client.setScreen(parent);
    }

    // Inner slider class for scale
    static class ScaleSlider extends SliderWidget {
        ScaleSlider(int x, int y, int width, int height, Text prefix, float initialValue) {
            super(x, y, width, height, prefix.copy().append(String.format("%.1fx", initialValue)),
                    (initialValue - 0.5f) / 1.5f); // normalize 0.5–2.0 to 0–1
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            float scale = 0.5f + (float) this.value * 1.5f;
            this.setMessage(Text.literal(String.format("Timer Scale: %.1fx", scale)));
        }

        @Override
        protected void applyValue() {
            BreedTimerConfig.scale = 0.5f + (float) this.value * 1.5f;
        }
    }
}
