package de.pt400c.defaultsettings.gui;

import java.util.function.Function;
import org.lwjgl.opengl.GL11;
import static de.pt400c.defaultsettings.FileUtil.MC;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class QuitButtonSegment extends ButtonSegment {

	public QuitButtonSegment(GuiScreen gui, float posX, float posY, int width, int height, Function<ButtonSegment, Boolean> function, boolean popup) {
		super(gui, posX, posY, "X", function, width, height, 0, popup);
	}
	
	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		Segment.drawRect(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), this.isSelected(mouseX, mouseY) ? 0xffbe2e2c : 0xffd85755, true, null, false);
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GlStateManager.disableAlphaTest();
		OpenGlHelper.glBlendFuncSeparate(770, 771, 1, 0);
		MC.fontRenderer.drawString(this.title, (float) (posX + this.getWidth() / 2 - 2), (float) (posY + this.getHeight() / 2 - 4), 0xffffffff);
		GlStateManager.enableAlphaTest();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();

	}


}
