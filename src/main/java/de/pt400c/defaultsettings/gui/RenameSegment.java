package de.pt400c.defaultsettings.gui;

import org.apache.commons.io.FileUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import de.pt400c.defaultsettings.FileUtil;
import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SharedConstants;
import static de.pt400c.defaultsettings.DefaultSettings.fontRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import static de.pt400c.neptunefx.NEX.*;
import java.io.File;
import java.io.IOException;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.opengl.GL11.*;

@OnlyIn(Dist.CLIENT)
public class RenameSegment extends Segment {
	
	protected boolean grabbed;
	public String query = "";
	public final String tex;
	protected boolean focused = false;
	private int cursorTimer = 0;
	boolean activated;
	private float flashingTimer = 0;
	int cursorPosition;
	private final int type;
	boolean denied = false;
	
	private static final String chars = "@^°\"§$%&/=?`´\\#+*'-}][{-_~";

	public RenameSegment(Screen gui, String text, float posX, float posY, int width, int height, boolean popupSegment, int type) {
		super(gui, posX, posY, width, height, popupSegment);
		this.tex = text;
		this.type = type;
	}
	
	public RenameSegment(Screen gui, String text, float posX, float posY, int width, int height, boolean popupSegment) {
		super(gui, posX, posY, width, height, popupSegment);
		this.tex = text;
		this.type = 1;
	}
	
	public static boolean isAllowedCharacter(char character)
    {
        return character != 167 && character >= ' ' && character != 127 && chars.indexOf(character) == -1;
    }
	
	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (p_keyPressed_1_ == GLFW_KEY_BACKSPACE) {
			if(this.query.length() < 1 || this.cursorPosition < 1)
				return false;
			
			String left = this.query.substring(0, cursorPosition);
			String right = this.query.substring(cursorPosition);
			
			left = left.substring(0, left.length() - 1);
			
			if(this.cursorPosition > 0)
				this.cursorPosition -= 1;
			
			this.query = left + right;
			
			this.activated = false;
			return true;
		} else if (p_keyPressed_1_ == GLFW_KEY_LEFT) {
			this.cursorPosition = MathUtil.clamp(this.cursorPosition - 1, 0, this.query.length());
			return true;
		} else if (p_keyPressed_1_ == GLFW_KEY_RIGHT) {
			this.cursorPosition = MathUtil.clamp(this.cursorPosition + 1, 0, this.query.length());
			return true;
		} else if (p_keyPressed_1_ == GLFW_KEY_ENTER || p_keyPressed_1_ == GLFW_KEY_KP_ENTER) {
			if (!this.query.isEmpty()) {
				this.activated = true;

			}
			if(this.type == 0) {
				GuiConfig gui = ((GuiConfig) this.gui);
				
				if(!gui.scrollableProfiles.profiles.contains(this.query.toLowerCase())) {
				
				ProfilesSegment parent2 = gui.scrollableProfiles;
				ContextMenuSegment parent = parent2.context;
		
				if(parent2.selectedName.equals(parent.id)) 
					parent2.selectedName = this.query;
				
				if(FileUtil.privateJson.currentProfile.equals(parent.id)) {
					FileUtil.activeProfile = this.query;
					FileUtil.privateJson.currentProfile = this.query;
					FileUtil.privateJson.save();
				}
				
				if(FileUtil.mainJson.mainProfile.equals(parent.id)) {
					FileUtil.mainJson.mainProfile = this.query;
					FileUtil.mainJson.save();
				}
				
				if(FileUtil.privateJson.targetProfile.equals(parent.id)) {
					FileUtil.privateJson.targetProfile = this.query;
					FileUtil.privateJson.save();
				}

				File fileDir = new File(FileUtil.getMainFolder(), parent.id);
				fileDir.renameTo(new File(FileUtil.getMainFolder(), this.query));

				parent2.guiContentUpdate(parent2.searchbar.query);
		
				gui.popup.setOpening(false);
			
				parent.backgroundTimer = 2.5F * (MathUtil.PI / 3);
				parent.setPos(-100, -100);
				}else {
					this.denied = true;
				}

			}else if(this.type == 1) {

				GuiConfig gui = ((GuiConfig) this.gui);
				if(!gui.scrollableProfiles.profiles.contains(this.query.toLowerCase())) {
					gui.popupField.setOpening(false);
					gui.scrollableProfiles.context.backgroundTimer = 2.5F * (MathUtil.PI / 3);
					gui.scrollableProfiles.context.setPos(-100, -100);

					File fileDir = new File(FileUtil.getMainFolder(), this.query);
					fileDir.mkdir();
					gui.scrollableProfiles.guiContentUpdate(gui.scrollableProfiles.searchbar.query);
				
					gui.scrollableProfiles.context.id = this.query;
				}else {
					this.denied = true;
				}
			} else if(this.type == 2) {
				GuiConfig gui = ((GuiConfig) this.gui);
				
				if(!gui.scrollableProfiles.profiles.contains(this.query.toLowerCase())) {
				
				ProfilesSegment parent2 = gui.scrollableProfiles;
				ContextMenuSegment parent = parent2.context;

				File fileDir = new File(FileUtil.getMainFolder(), this.tex);
				File fileDir2 = new File(FileUtil.getMainFolder(), this.query);
				try {
					FileUtils.copyDirectory(fileDir, fileDir2);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				parent2.guiContentUpdate(parent2.searchbar.query);
				
				gui.scrollableProfiles.context.id = this.query;
		
				gui.popup.setOpening(false);
			
				parent.backgroundTimer = 2.5F * (MathUtil.PI / 3);
				parent.setPos(-100, -100);
				}else {
					this.denied = true;
				}

			}

			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
		if (isAllowedCharacter(p_charTyped_1_)) {
			final String s1 = SharedConstants.filterAllowedCharacters(Character.toString(p_charTyped_1_));
			if (this.query.isEmpty() && s1.equals(" "))
				return true;

			if(this.query.length() > 30)
				return false;
			
			String left = this.query.substring(0, cursorPosition);
			String right = this.query.substring(cursorPosition);
			
			++this.cursorPosition;
			
			this.query = left + s1 + right;
	
			this.activated = false;
			return true;

		} else {
			return false;
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.enableBlend();
		GlStateManager.disableTexture();
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		flashingTimer += 0.07;
		final float darken = (float) ((Math.sin(flashingTimer - MathUtil.PI / 2) + 1) / 4 + 0.5);

		int color = 0;
		
		String text = this.query;
		float dots = fontRenderer.getStringWidth("...", 1, false);

		float widthString = fontRenderer.getStringWidth(text, 1, false);

		if (widthString >= this.gui.width - 190) 
			text = fontRenderer.trimStringToWidth(text, (int) (this.gui.width - 190 - 1 - dots), false) + "...";

		MenuScreen menu = ((GuiConfig) this.gui).menu;

		if (menu.getVariants().get(menu.index).selected == this)
			this.focused = true;
		else
			this.focused = false;

		float diff = this.focused ? 1.5F : 1;

		drawRectRoundedCorners(this.getPosX() - diff, this.getPosY() - diff, this.getPosX() + this.getWidth() + diff, this.getPosY() + this.getHeight() + diff, this.denied ? 0xffd85755 : 0xffe6e6e6, Integer.MAX_VALUE);
		
		drawRectRoundedCorners(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), 0xff3c3c3c, Integer.MAX_VALUE);

		this.cursorTimer++;
		if (this.cursorTimer > 80)
			this.cursorTimer = 0;

		if (this.cursorTimer <= 40 && this.focused) {

			color = 0xffa0a0a0;

			float f3 = (float) (color >> 24 & 255) / 255.0F;
			float f = (float) (color >> 16 & 255) / 255.0F;
			float f1 = (float) (color >> 8 & 255) / 255.0F;
			float f2 = (float) (color & 255) / 255.0F;

			GlStateManager.color4f(f, f1, f2, f3);

			drawRect(this.getPosX() + 5 + fontRenderer.getStringWidth(text.substring(0, this.cursorPosition), 1, false), this.getPosY() + 4, this.getPosX() + 5.5F + fontRenderer.getStringWidth(text.substring(0, this.cursorPosition), 1, false), this.getPosY() + this.getHeight() - 4, null, false, null, false);
		}
		
		GlStateManager.disableBlend();
		GlStateManager.enableTexture();
		glPushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.glBlendFuncSeparate(770, 771, 1, 0);

		if (this.query.isEmpty())
			fontRenderer.drawString(tex, (float) (this.getPosX() + 5), (float) (this.getPosY() + 5), this.focused && !this.activated ? darkenColor(0xffe6e6e6, darken).getRGB() : 0xffe6e6e6, 1, false);
		else
			fontRenderer.drawString(text, (float) (this.getPosX() + 5), (float) (this.getPosY() + 5), 0xffe6e6e6, 1, false);
		GlStateManager.disableBlend();
		glPopMatrix();
	}
	
	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int button) {

		if (this.isSelected(mouseX, mouseY)) {
			MenuScreen menu = ((GuiConfig) this.gui).menu;
			menu.getVariants().get(menu.index).selected = this;
			this.grabbed = true;
			
			if(this.query.isEmpty())
				return true;
			
			this.cursorPosition = fontRenderer.trimStringToWidth(this.query, (int)(mouseX - (this.getPosX() + this.hitX + 5)), false).length();

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

		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
}