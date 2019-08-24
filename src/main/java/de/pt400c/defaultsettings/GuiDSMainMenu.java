package de.pt400c.defaultsettings;

import static de.pt400c.defaultsettings.FileUtil.MC;
import java.awt.Color;
import java.util.ArrayList;
import de.pt400c.defaultsettings.gui.ButtonSegment;
import de.pt400c.defaultsettings.gui.DefaultSettingsGUI;
import de.pt400c.defaultsettings.gui.TextSegment;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TextFormatting;
import static de.pt400c.neptunefx.NEX.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiDSMainMenu extends DefaultSettingsGUI {
	
    public final Screen parentScreen;

    public GuiDSMainMenu(Screen parentScreen) {
    	super(new TranslationTextComponent("options.title"));
    	this.minecraft = MC;
        this.parentScreen = parentScreen;
	}
    
    @Override
    public void init()  {
    	this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.clearSegments();
        
        this.addSegment(new TextSegment(this, this.width / 2 - MC.fontRenderer.getStringWidth("- DefaultSettings -") / 2, 9, 0, 0, "- DefaultSettings -", 0x0, false));
        
        final String text = "This is the first bootup of " + TextFormatting.BOLD + "DefaultSettings" + TextFormatting.RESET + ". In order to assure proper functionality, you should consider the following:"
        		+ " Automatically this mod ships all mod configs and doesn't replace them when you as the modpack's creator update the configs. Also neither the default keybindings, options nor the default servers are shipped by default. For the most cases that is not optional. Please customise DS by opening the management GUI (F7 + G in the mods list or click on the 'Config' button when selecting DS in that list).\n" + TextFormatting.RED + TextFormatting.BOLD+"Important" + TextFormatting.RESET +": Once you finished configuring the modpack, you have to activate the 'Export Mode' in that GUI!";
        
        final ArrayList<String> lines = new ArrayList<String>();
		
		final int textWidth = MC.fontRenderer.getStringWidth(text);
		if(textWidth > this.width - 20) 
			lines.addAll(MC.fontRenderer.listFormattedStringToWidth(text, this.width - 20));
		else 
			lines.add(text);
		
        this.addSegment(new TextSegment(this, 10, 35, 0, 0, String.join("\n", lines), 0x0, 14, false));
    	
    	this.addSegment(new ButtonSegment(this, 70, this.height - 50, "Later", button -> {
    		FileUtil.setPopup(true);
    		GuiDSMainMenu.this.minecraft.displayGuiScreen(GuiDSMainMenu.this.parentScreen);
    		return true;}, 80, 25, 3));
    	
    	this.addSegment(new ButtonSegment(this, this.width - 80 - 70, this.height - 50, "Dismiss", button -> {
    		FileUtil.setPopup(false);
    		GuiDSMainMenu.this.minecraft.displayGuiScreen(GuiDSMainMenu.this.parentScreen);
    		return true;}, 80, 25, 3));
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
    	AbstractGui.fill(0, 0, this.width, this.height, Color.WHITE.getRGB());
    	glDisable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glDisable(GL_ALPHA_TEST);
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
		glShadeModel(GL_SMOOTH);
		drawGradient(0, 25, width, 30, 0xffb3b3b3, 0x00ffffff, 1);
		glShadeModel(GL_FLAT);
		glDisable(GL_BLEND);
		glDisable(GL_ALPHA_TEST);
		glEnable(GL_TEXTURE_2D);
		AbstractGui.fill(0, 0, width, 25, 0xffe0e0e0);
    	super.render(mouseX, mouseY, partialTicks);
    }
}