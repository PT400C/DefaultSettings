package de.pt400c.defaultsettings.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import static de.pt400c.defaultsettings.FileUtil.MC;
import static de.pt400c.neptunefx.NEX.*;
import static de.pt400c.defaultsettings.DefaultSettings.fontRenderer;
import java.util.function.Function;

import com.mojang.blaze3d.platform.GlStateManager;

import de.pt400c.defaultsettings.DefaultSettings;
import de.pt400c.defaultsettings.GuiConfig;
import de.pt400c.defaultsettings.GuiConfig.HeaderPart;
import static org.lwjgl.opengl.GL11.*;

@OnlyIn(Dist.CLIENT)
public class HelpSegment extends BakedSegment {
	
	private final Function<GuiConfig, Integer> posXF;
	public float timer = 0;
	protected boolean grabbed;
    private float processFactor;
    public float flickerTimer = 0;
	private final ResourceLocation icon = new ResourceLocation(DefaultSettings.MODID, "textures/gui/about.png");
	private boolean doIt;
	private boolean selected;
	
	public HelpSegment(Screen gui, Function<GuiConfig, Integer> posX, float posY) {
		super(gui, 0, posX.apply((GuiConfig) gui), posY, 45, 25, 44, 44, 44, true, false);
		this.posXF = posX;
	}
	
	public static void openPopup(GuiConfig gui) {
		MenuScreen menu = gui.menu;
		menu.getVariants().get(menu.index).selected = null;
		
		gui.popup.setOpening(true, 320, 210);
		gui.popup.getWindow().title = "Help: " + HeaderPart.tabName;
		gui.popup.getWindow().setPos(gui.width / 2 - 310 / 2, gui.height / 2 - 200 / 2);
		gui.popupField = gui.popup;
		gui.popupField.getWindow().clearChildren();
		final String end = "\n\n\u00a7cIMPORTANT\u00a7r: Do \u00a74NOT\u00a7r forget to \u00a7aactivate\u00a7r the\n'export mode' before exporting your modpack with the\nTwitch Launcher (or any other launcher).\n\n\u00a76Still facing problems? Contact PT400C#8363 on Discord\u00a7r";
		if(gui.menu.index == 0) {
			gui.popupField.getWindow().addChild(new TextSegment(gui, 5, 29, 0, 0, "DefaultSettings can ship \u00a7bdefault options \u00a7r(options.txt\nminus keybindings + optionsof.txt), \u00a7bdefault keybindings\u00a7r\n(options.txt) and \u00a7bdefault servers \u00a7r(servers.dat).\n\nThese three different types of configuration will then\nonly be overridden once on the first time your players\nstart up the modpack.\n In case the respective configuration you want to\nupdate already exists, DS will also prompt you to\nproceed and override the existing configuration." + end, 0xffffffff, true));
		}else if(gui.menu.index == 1) {
			gui.popupField.getWindow().addChild(new TextSegment(gui, 5, 29, 0, 0, "In addition DefaultSettings can also deal with config\n\u00a7bfiles\u00a7r and \u00a7bfolders\u00a7r used by your modpack.\n Just like the configurations from the 'Save' tab,\neverything which has been ticked will be shipped to the\nmodpack's user \u00a7aas you command\u00a7r.\n\nIf you want to update specific configs, just change\nthem locally and export the modpack again. This\nallows you to force the config to get overridden on the\nclient's side once when they update the modpack." + end, 0xffffffff, true));
		}else if(gui.menu.index == 2) {
			gui.popupField.getWindow().addChild(new TextSegment(gui, 5, 29, 0, 0, "Here you can create and modify certain \u00a7bprofiles\u00a7r or\n\u00a7binstances\u00a7r of DefaultSettings's configs. The profile\nmarked as main (blue dot) will be used by your users by\ndefault, eventhough they may switch their local profile." + end, 0xffffffff, true));
		}else {
			gui.popupField.getWindow().addChild(new TextSegment(gui, 5, 29, 0, 0, "Nothing here, I guess? See ya." + end, 0xffffffff, true));
		}
		
		gui.popupField.getWindow().addChild(new QuitButtonSegment(gui, 290, 5, 14, 14, quitButton -> {

			gui.popupField.setOpening(false);

			return true;
		}, 3F, true));
		gui.popup.setVisible(true);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		
		if(resized != this.resized_mark) 
			posX = posXF.apply((GuiConfig) this.gui);
		
		setup();
		
		this.selected = this.isSelected(mouseX, mouseY);

		if (this.selected) {
			
			if (this.timer <= MathUtil.PI / 3) {
				this.timer += 0.05;
				doIt = true;
			}

		} else {

			if (this.timer > 0) {
				this.timer -= 0.05;
				doIt = true;
			}
		}
		
		if(doIt) {
			compiled = false;
			doIt = false;
		}
		
		if(!compiled) {
			
			this.processFactor = (float) ((Math.sin(3 * this.timer - 3 * (MathUtil.PI / 2)) + 1) / 2);
			
			preRender();

			GlStateManager.enableBlend();
	    	GlStateManager.disableAlphaTest();
	    	GlStateManager.disableTexture();
	    	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

			drawRectRoundedCorners(0, 0, this.width, this.height, calcAlpha(0xff696969, this.processFactor).getRGB(), 4);
	
			GlStateManager.enableTexture();
	    	
			MC.getTextureManager().bindTexture(icon);
			glColor3f(1, 1, 1);
			GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
			GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
			drawScaledTex(2, this.height / 2 - 16 / 2, (int) 16, (int) 16);
			GlStateManager.disableBlend();
			
			fontRenderer.drawString("Help", 21, 9, 0xffffffff, 0.8F, true);
			GlStateManager.enableAlphaTest();
    	
		postRender(1, false);
		}
		glPushMatrix();

		drawTexture(1);
		glPopMatrix();

	}
	
	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int button) {

		if (this.isSelected(mouseX, mouseY)) {
			this.grabbed = true;
			((DefaultSettingsGUI) this.gui).resetSelected();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseDragged(int mouseX, int mouseY, int button) {
		if (!this.isSelected(mouseX, mouseY))
			this.grabbed = false;
		return super.mouseDragged(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(int mouseX, int mouseY, int button) {
		if (this.grabbed) {
			if (this.isSelected(mouseX, mouseY))
				this.grabbed = false;

			
			this.clickSound();
			openPopup((GuiConfig) this.gui);

		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
}