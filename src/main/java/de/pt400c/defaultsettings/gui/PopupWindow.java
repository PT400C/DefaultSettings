package de.pt400c.defaultsettings.gui;

import static de.pt400c.defaultsettings.FileUtil.MC;
import java.util.ArrayList;
import java.util.List;
import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import static de.pt400c.neptunefx.NEX.*;
import static org.lwjgl.opengl.GL11.*;
import static de.pt400c.neptunefx.DrawString.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PopupWindow extends Segment {
	
	private List<Segment> children = new ArrayList<>();
	public String title;
	public float alphaRate;
	private boolean dragging;
	private float distanceX = 0;
	private float distanceY = 0;

	public PopupWindow(GuiScreen gui, float posX, float posY, float width, float height, String title) {
		super(gui, posX, posY, width, height, true);
		this.title = title;
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		
		glEnable(GL_BLEND);
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
	    glDisable(GL_ALPHA_TEST);
	    
	 	glShadeModel(GL_SMOOTH);
		glDisable(GL_TEXTURE_2D);

		drawGradient(this.getPosX() + this.width - 10, this.getPosY() + 10, this.getPosX() + this.width + 5, this.getPosY() + this.height - 10, 0xff000000, 0x00101010, 0);
		
		drawGradient(this.getPosX() - 5, this.getPosY() + 10, this.getPosX() + 10, this.getPosY() + this.height - 10, 0xff000000, 0x00101010, 2);
		
		drawGradient(this.getPosX() + 10, this.getPosY() - 5, this.getPosX() + this.width - 10, this.getPosY() + 10, 0xff000000, 0x00101010, 3);
		
		drawGradient(this.getPosX() + 10, this.getPosY() + this.height - 10, this.getPosX() + this.width - 10, this.getPosY() + this.height + 5, 0xff000000, 0x00101010, 1);
		
		drawGradientCircle((float) this.getPosX() + 10, (float) this.getPosY() + 10, 15, 180, 75, 0xff000000, 0x00101010);
		
		drawGradientCircle((float) this.getPosX() + this.width - 10, (float) this.getPosY() + 10, 15, 270, 75, 0xff000000, 0x00101010);
		
		drawGradientCircle((float) this.getPosX() + this.width - 10, (float) this.getPosY() + this.height - 10, 15, 0, 75, 0xff000000, 0x00101010);
		
		drawGradientCircle((float) this.getPosX() + 10, (float) this.getPosY() + this.height - 10, 15, 90, 75, 0xff000000, 0x00101010);
		
		glEnable(GL_TEXTURE_2D);
		glShadeModel(GL_FLAT);
		glEnable(GL_ALPHA_TEST);
		glDisable(GL_BLEND);
		
		drawRectRoundedUpper((float) this.posX, (float) this.posY, (float) (this.posX + width), (float) (this.posY + 24), 0xff8b8b8b);
		drawRectRoundedLower((float) this.posX, (float) this.posY + 24, (float) (this.posX + width), (float) (this.posY + height), 0xfffbfbfb);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_DST_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    glDisable(GL_ALPHA_TEST);
	 	glShadeModel(GL_SMOOTH);
		glDisable(GL_TEXTURE_2D);

		drawGradient(this.getPosX(), this.getPosY() + 24, this.getPosX() + this.width, this.getPosY() + 24 + 5, 0xff606060, 0x00404040, 1);

		glEnable(GL_TEXTURE_2D);
		glShadeModel(GL_FLAT);
		glEnable(GL_ALPHA_TEST);

		glDisable(GL_BLEND);
		drawString(this.title, (float) (this.getPosX() + this.getWidth() / 2 + 1 - MC.fontRenderer.getStringWidth(this.title) / 2), (float) (this.getPosY() + 9), 0xff1b1b1b);

		synchronized (this.children) {
			this.children.forEach(segment -> segment.render(mouseX, mouseY, partialTicks));

			this.children.forEach(segment -> segment.hoverCheck(mouseX, mouseY));
		}

		if (this.dragging) {

			final float origX = this.posX;
			final float origY = this.posY;
			
			this.posX = mouseX - distanceX;
			this.posY = mouseY - distanceY;
			
			if((this.posX - origX) == 0 && (this.posY - origY) == 0)
				return;
			
			this.children.forEach(segment -> segment.setPos(segment.posX + (this.posX - origX), segment.posY + (this.posY - origY)));
		}
	}
	
	public boolean isSelectedLower(int mouseX, int mouseY) {
		return (mouseX >= this.getPosX() && mouseY >= this.getPosY() + 10 && mouseX < this.getPosX() + this.getWidth() && mouseY < this.getPosY() + height) || (mouseX >= this.getPosX() + 10 && mouseY >= this.getPosY() && mouseX < this.getPosX() + this.getWidth() - 10 && mouseY < this.getPosY() + 10) || (distanceBetweenPoints((float) this.getPosX() + 10F, (float) this.getPosY() + 10F, (float) mouseX, (float) mouseY) <= 10) || (distanceBetweenPoints((float) this.getPosX() + this.getWidth() - 10F, (float) this.getPosY() + 10F, (float) mouseX, (float) mouseY) <= 10);
	}
	
	@Override
	public boolean isSelected(int mouseX, int mouseY) {
		return (mouseX >= this.getPosX() && mouseY >= this.getPosY() + 10 && mouseX < this.getPosX() + this.getWidth() && mouseY < this.getPosY() + 24) || (mouseX >= this.getPosX() + 10 && mouseY >= this.getPosY() && mouseX < this.getPosX() + this.getWidth() - 10 && mouseY < this.getPosY() + 10) || (distanceBetweenPoints((float) this.getPosX() + 10F, (float) this.getPosY() + 10F, (float) mouseX, (float) mouseY) <= 10) || (distanceBetweenPoints((float) this.getPosX() + this.getWidth() - 10F, (float) this.getPosY() + 10F, (float) mouseX, (float) mouseY) <= 10);
	}
	
	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (!this.isSelectedLower(mouseX, mouseY))
			((GuiConfig) this.gui).popupField.setOpening(false);

		synchronized (this.children) {
			for (Segment segment : children) {
				if (segment.mouseClicked(mouseX, mouseY, mouseButton))
					return true;

			}
		}

		if (this.isSelected(mouseX, mouseY)) {
			this.dragging = true;
			this.distanceX = (mouseX - this.posX);
			this.distanceY = (mouseY - this.posY);

			return true;
		} else
			return false;
	}
	
	@Override
	public boolean mouseDragged(int p_mouseDragged_1_, int p_mouseDragged_3_, int p_mouseDragged_5_) {
			synchronized (this.children) {
				for (Segment segment : this.children) {
					if (segment.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_)) 
						break;

				}
			}
		return false;
	}
	
	@Override
	public boolean mouseReleased(int p_mouseReleased_1_, int p_mouseReleased_3_, int p_mouseReleased_5_) {
			synchronized (this.children) {
				for (Segment segment : this.children) {
					if (segment.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)) 
						return true;

				}
			}
			this.dragging = false;
			
		return false;
	}

	public PopupWindow addChild(Segment segment) {
		synchronized (this.children) {
			this.children.add(segment.setPos(this.posX + segment.posX, this.posY + segment.posY));
		}
		return this;
	}

	public void clearChildren() {
		synchronized (this.children) {
			this.children.clear();
		}
	}
	
	public List<Segment> getChildren() {
		return this.children;
	}

}