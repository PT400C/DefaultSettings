package de.pt400c.defaultsettings;

import static de.pt400c.defaultsettings.FileUtil.MC;
import java.awt.Color;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL30.*;
import de.pt400c.defaultsettings.gui.*;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.math.MathHelper;
import static de.pt400c.neptunefx.NEX.*;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.*;
import net.minecraftforge.versions.mcp.MCPVersion;

@OnlyIn(Dist.CLIENT)
public class GuiConfig extends DefaultSettingsGUI {

    public final Screen parentScreen;
    public LeftMenu leftMenu;
    public PopupSegment popup;
    public ButtonSegment buttonS;
    public ButtonSegment buttonK;
    public ButtonSegment buttonO;
    public ButtonMenuSegment selectedSegment = null;
    private ExecutorService tpe = new ThreadPoolExecutor(1, 3, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    private ButtonState[] cooldowns = new ButtonState[] {new ButtonState(false, 0), new ButtonState(false, 0), new ButtonState(false, 0)};
    public FramebufferObject framebufferMc;
    private boolean compiled;
    private int storeWidth;
	private int storeHeight;
    private int gcAmount;
    private int bgDPLList = -1;
    
    public GuiConfig(Screen parentScreen) {
    	super(new TranslationTextComponent("options.title"));
    	this.minecraft = MC;
        this.parentScreen = parentScreen;
	}
    
    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
    	
    	if(p_keyPressed_1_ == 256 && this.popupField != null){
    		this.popupField.setOpening(false);
    		return true;
    	}
		return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }

    public void initGui() {
    	
    	if(this.framebufferMc != null) {
			glDeleteRenderbuffers(this.framebufferMc.colorBuffer);
	        glDeleteFramebuffers(this.framebufferMc.framebufferObject);
		}
			
		this.compiled = false;
    
    	this.framebufferMc = new FramebufferObject(MC.mainWindow.getWidth(), MC.mainWindow.getHeight());
    	
    	this.clearSegments();
    	
    	if(this.gcAmount == 9) {
    		System.gc();
    		this.gcAmount = 0;
    	}
    		
    	this.gcAmount++;
    	
    	this.addSegment(new QuitButtonSegment(this, this.width - 22, 2, 20, 20, button -> {
    		
    		GuiConfig.this.minecraft.displayGuiScreen(GuiConfig.this.parentScreen);
    		return true;}, false));
    	
    	this.menu = new MenuScreen(this, 74, 25);
    	
    	this.leftMenu = new LeftMenu(this, 0, 25, 46, this.height - 25);

    	this.addSegment(this.menu.
        		addVariant(new MenuArea(this, 74, 25).
        				addChild(this.buttonO = new ButtonSegment(this, this.menu.getWidth() / 2 - 60, this.menu.getHeight() / 2 - 30, "Save options", button -> {
        	    			tpe.execute(new Runnable() {
        	    				@Override
        	    				public void run() {
        	    					saveOptions();
        	    				}
        	    			});
        				return true;
                
        	}, 80, 25, 3, "Save all default game options")).
        				
        				addChild(this.buttonS = new ButtonSegment(this, this.menu.getWidth() / 2 + 52, this.menu.getHeight() / 2 - 30, "Save servers", button -> {
        	    			tpe.execute(new Runnable() {
        	    				@Override
        	    				public void run() {
        	    					saveServers();
        	    				}
        	    			});
            				return true;
                    
            	}, 80, 25, 3, "Save your servers")). 
        				addChild(this.buttonK = new ButtonSegment(this, this.menu.getWidth() / 2 - 167, this.menu.getHeight() / 2 - 30, "Save keys", button -> {
        					tpe.execute(new Runnable() {
        						@Override
        						public void run() {
        							saveKeys();
        						}
        					});
            				return true;
                    
            	}, 80, 25, 3, "Save keybindings"))
        				).addVariant(new MenuArea(this, 74, 25).
        						
        				addChild(new ScrollableSegment(this, 20, 30, width - 74 - 90, height - 25 - 10 - 30, (byte) 0))).addVariant(new MenuArea(this, 74, 25).

        				addChild(new TextSegment(this, 10, 20, 20, 20, "DefaultSettings: " + MCPVersion.getMCVersion() + "-" + DefaultSettings.VERSION + "\n\nBuild-ID: " + DefaultSettings.BUILD_ID + "\n\nBuild-Time: " + DefaultSettings.BUILD_TIME + "\n\n\nCreated by Jomcraft Network, 2019", 0, false))));

    	
    	this.addSegment(this.leftMenu.addChild(new ButtonMenuSegment(0, this, 10, 9, "Save", button -> {return true;}, this.leftMenu, "textures/gui/save.png").setActive(true, false)).addChild(new ButtonMenuSegment(1, this, 10, 35, "Configs", button -> {return true;}, this.leftMenu, "textures/gui/config.png")).addChild(new ButtonMenuSegment(2, this, 10, 61, "About", button -> {return true;}, this.leftMenu, "textures/gui/about.png")).addChild(new SplitterSegment(this, 72, 7, this.height - 42, this.leftMenu))/*.addChild(new IconSegment(this, 10, 11, 16, 16, "textures/gui/test.png", this.leftMenu))*/.addChild(new ButtonUpdateChecker(this, /*72 / 2 - 20 / 2, */this.height - 30 - 25, this.leftMenu)));
    	
    	this.addSegment(new ExportSwitchSegment(this, 160, 7));
    	
    	this.addSegment(this.popup = new PopupSegment(this, 0, 0, this.width, this.height).setWindow(new PopupWindow(this, this.width / 2 - 210 / 2, this.height / 2 - 100 / 2, 210, 100, "").addChild(new QuitButtonSegment(this, 190, 5, 14, 14, button -> {
    		
    		GuiConfig.this.popupField.setOpening(false);
    		
    		return true;}, true))));
    	
    	this.popupField = null;
    	
    	this.minecraft.keyboardListener.enableRepeatEvents(true);
    	
    	super.init();
    }
    
    public void copyConfigs() {
		
		tpe.execute(new Runnable() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				GuiConfig.this.menu.exportActive.setByte((byte) 0);
				try {
					FileUtil.restoreConfigs();
				} catch (IOException e) {
					if(e instanceof ClosedByInterruptException)
						return;
					DefaultSettings.getInstance().log.log(Level.ERROR, "An exception occurred while trying to move the configs:", e);
				}
				GuiConfig.this.menu.exportActive.setByte((byte) 1);
				for(MenuArea variant : GuiConfig.this.menu.getVariants()) {
					variant.getChildren().stream().filter(segment -> segment instanceof ScrollableSegment).forEach(segment -> segment.guiContentUpdate(((ScrollableSegment) segment).searchbar.query));
				}
			}
		});
	}
	
	public void deleteConfigs() {
		
		if(!FileUtil.exportMode()) {
			this.popup.setOpening(true);
			this.popup.getWindow().title = "Delete Config Folder";
			this.popup.getWindow().setPos(this.width / 2 - 210 / 2, this.height / 2 - 100 / 2);
			this.popupField = this.popup;
			this.popupField.getWindow().clearChildren();
			this.popupField.getWindow().addChild(new TextSegment(this, 5, 30, 20, 20, "Do you want to delete every file\nfrom the config folder?\n\n(The defaultsettings folder will be kept)", 0, true));
			this.popupField.getWindow().addChild(new QuitButtonSegment(this, 190, 5, 14, 14, button -> {

				GuiConfig.this.popupField.setOpening(false);

				return true;
			}, true));

			this.popupField.getWindow().addChild(new ButtonSegment(this, 105 - 80, 75, "Proceed", button -> {

				GuiConfig.this.popupField.setOpening(false);
				tpe.execute(new Runnable() {
					@SuppressWarnings("static-access")
					@Override
					public void run() {
						GuiConfig.this.menu.exportActive.setByte((byte) 0);
						try {
							FileUtil.setExportMode();
						} catch (IOException e) {
							if(e instanceof ClosedByInterruptException)
								return;
							DefaultSettings.getInstance().log.log(Level.ERROR, "An exception occurred while trying to move the configs:", e);
						}
						GuiConfig.this.menu.exportActive.setByte((byte) 2);
						for(MenuArea variant : GuiConfig.this.menu.getVariants()) {
							variant.getChildren().stream().filter(segment -> segment instanceof ScrollableSegment).forEach(segment -> segment.guiContentUpdate(((ScrollableSegment) segment).searchbar.query));
						}
					}
				});

				return true;
			}, 60, 20, 2, null, true));
			
			this.popupField.getWindow().addChild(new ButtonSegment(this, 105 + 20, 75, "Move", button -> {

				GuiConfig.this.popupField.setOpening(false);
				tpe.execute(new Runnable() {
					@SuppressWarnings("static-access")
					@Override
					public void run() {
						GuiConfig.this.menu.exportActive.setByte((byte) 0);
						try {
							FileUtil.moveAllConfigs(true);
						} catch (IOException e) {
							if(e instanceof ClosedByInterruptException)
								return;
							DefaultSettings.getInstance().log.log(Level.ERROR, "An exception occurred while trying to move the configs:", e);
						}
						GuiConfig.this.menu.exportActive.setByte((byte) (FileUtil.exportMode() ? 2 : 1));
						for(MenuArea variant : GuiConfig.this.menu.getVariants()) {
							variant.getChildren().stream().filter(segment -> segment instanceof ScrollableSegment).forEach(segment -> segment.guiContentUpdate(((ScrollableSegment) segment).searchbar.query));
						}
					}
				});

				return true;
			}, 60, 20, 2, "Move all contents from the config folder to DS's config management", true));
			
			this.popup.isVisible = true;
			
		}else {
			tpe.execute(new Runnable() {
				@SuppressWarnings("static-access")
				@Override
				public void run() {
					try {
						FileUtil.setExportMode();
					} catch (IOException e) {
						if(e instanceof ClosedByInterruptException)
							return;
						DefaultSettings.getInstance().log.log(Level.ERROR, "An exception occurred while trying to move the configs:", e);
					}
					GuiConfig.this.menu.exportActive.setByte((byte) 2);
					for(MenuArea variant : GuiConfig.this.menu.getVariants()) {
						variant.getChildren().stream().filter(segment -> segment instanceof ScrollableSegment).forEach(segment -> segment.guiContentUpdate(((ScrollableSegment) segment).searchbar.query));
					}
				}
			});
		}
	}
    
    public void changeSelected(ButtonMenuSegment segment) {
    	if(this.selectedSegment != null && this.selectedSegment != segment)
    		this.selectedSegment.setActive(false, true);
    	this.selectedSegment = segment;
    	this.menu.setIndex(segment.id);
    }

    @Override
    public void onClose() {
    	this.minecraft.keyboardListener.enableRepeatEvents(false);
    	tpe.shutdownNow();
    	if(framebufferMc != null)
    		framebufferMc.deleteFramebuffer();
    	super.onClose();
    }

    @Override
    public void tick() {
    	super.tick();
        for(int id = 0; id < cooldowns.length; id++) {
        	if(cooldowns[id].renderCooldown > 0)
        		cooldowns[id].renderCooldown--;
        	else if(cooldowns[id].renderCooldown < 0) 
        		cooldowns[id].renderCooldown++;	
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
    	
    	if(MC.mainWindow.getWidth() != this.storeWidth || MC.mainWindow.getHeight() != this.storeHeight) {
    		this.storeWidth = MC.mainWindow.getWidth();
    		this.storeHeight = MC.mainWindow.getHeight();
    		initGui();
    	}
    	
    	this.minecraft.getFramebuffer().unbindFramebuffer();
		glPushMatrix();
		glClear(16640);
		this.framebufferMc.bindFramebuffer(true);
		glEnable(GL_MULTISAMPLE);
		glEnable(GL_TEXTURE_2D);
		glClear(256);
		glMatrixMode(5889);
		glLoadIdentity();
		glOrtho(0.0D, MC.mainWindow.getScaledWidth(), MC.mainWindow.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
		glMatrixMode(5888);
		glLoadIdentity();
		glTranslatef(0.0F, 0.0F, -2000.0F);
		glClear(256);
		AbstractGui.fill(0, 0, this.width, this.height, Color.WHITE.getRGB());
		
		if (compiled) 
			glCallList(this.bgDPLList);
		else {
			this.bgDPLList = GLAllocation.generateDisplayLists(1);
			glNewList(this.bgDPLList, GL_COMPILE);

			glDisable(GL_TEXTURE_2D);
			glEnable(GL_BLEND);
			glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
			glShadeModel(GL_SMOOTH);

			drawGradient(72, 25, this.width, 30, 0xffaaaaaa, 0x00ffffff, 1);
		
			drawGradient(0, 25, 72, 30, 0xff7c7c7c, 0x00ffffff, 1);

			glShadeModel(GL_FLAT);
			glDisable(GL_BLEND);
			glEnable(GL_TEXTURE_2D);
		
			AbstractGui.fill(0, 0, 72, 25, 0xff9f9f9f);
        
			AbstractGui.fill(72, 0, width, 25, 0xffe0e0e0);
			this.font.drawStringWithShadow("Tab", MathHelper.clamp(72 / 2 - (this.font.getStringWidth("Tab") / 2), 0, Integer.MAX_VALUE), 10, 16777215);
        
        	final int posX = MathHelper.clamp((this.width - 74) / 2 + 74 - (this.font.getStringWidth("- DefaultSettings -") / 2), 74, Integer.MAX_VALUE);
        
        	this.font.drawString("- DefaultSettings -", posX + 1, 10 + 1, Color.WHITE.getRGB());
        
        	this.font.drawString("- DefaultSettings -", posX, 10, 0xff5d5d5d);

        	glEndList();
			compiled = true;
			glCallList(this.bgDPLList);
		}
        
        this.buttonS.color = cooldowns[1].getProgress() ? 0xffccab14 : cooldowns[1].renderCooldown < 0 ? 0xffcc1414 : cooldowns[1].renderCooldown > 0 ? 0xff5dcc14 : 0xffa4a4a4;
        this.buttonK.color = cooldowns[2].getProgress() ? 0xffccab14 : cooldowns[2].renderCooldown < 0 ? 0xffcc1414 : cooldowns[2].renderCooldown > 0 ? 0xff5dcc14 : 0xffa4a4a4;
        this.buttonO.color = cooldowns[0].getProgress() ? 0xffccab14 : cooldowns[0].renderCooldown < 0 ? 0xffcc1414 : cooldowns[0].renderCooldown > 0 ? 0xff5dcc14 : 0xffa4a4a4;
        super.render(mouseX, mouseY, partialTicks);
        
        this.framebufferMc.unbindFramebuffer();
        glPopMatrix();

		this.minecraft.getFramebuffer().bindFramebuffer(true);
		glPushMatrix();
		glBindFramebuffer(GL_READ_FRAMEBUFFER, framebufferMc.framebufferObject);
		glBlitFramebuffer(0, 0, MC.mainWindow.getWidth(), MC.mainWindow.getHeight(), 0, 0, MC.mainWindow.getWidth(), MC.mainWindow.getHeight(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
		glPopMatrix();
    }
    
    public void saveServers() {
		if (FileUtil.serversFileExists()) {
			this.popup.setOpening(true);
			this.popup.getWindow().title = "Save Servers";
			this.popup.getWindow().setPos(this.width / 2 - 210 / 2, this.height / 2 - 100 / 2);
			this.popupField = this.popup;
			this.popupField.getWindow().clearChildren();
			this.popupField.getWindow().addChild(new TextSegment(this, 5, 30, 20, 20, "The server list already exists\n\nWould you like to overwrite it?", 0, true));
			this.popupField.getWindow().addChild(new QuitButtonSegment(this, 190, 5, 14, 14, button -> {

				GuiConfig.this.popupField.setOpening(false);

				return true;
			}, true));

			this.popupField.getWindow().addChild(new ButtonSegment(this, 105 - 30, 75, "Overwrite", button -> {
				GuiConfig.this.cooldowns[1].setProgress(true);

				GuiConfig.this.popupField.setOpening(false);
				tpe.execute(new Runnable() {
					@Override
					public void run() {
						try {
							FileUtil.saveServers();
							GuiConfig.this.cooldowns[1].renderCooldown = 30;
						} catch (ClosedByInterruptException e) {
							DefaultSettings.log.log(Level.DEBUG, "An exception occurred while saving the server list: Interruption exception");
						} catch (Exception e) {
							DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the server list:", e);
							GuiConfig.this.cooldowns[1].renderCooldown = -30;
						}
						GuiConfig.this.cooldowns[1].setProgress(false);
					}
				});
				return true;
			}, 60, 20, 2, null, true));

			this.popup.isVisible = true;
		} else {

			GuiConfig.this.cooldowns[1].setProgress(true);

			try {
				FileUtil.saveServers();
				this.cooldowns[1].renderCooldown = 30;
			} catch (ClosedByInterruptException e) {
				DefaultSettings.log.log(Level.DEBUG, "An exception occurred while saving the server list: Interruption exception");
			} catch (Exception e) {
				DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the server list:", e);
				this.cooldowns[1].renderCooldown = -30;
			}
			this.cooldowns[1].setProgress(false);

		}

	}
    
    public void saveOptions() {
		if (FileUtil.optionsFilesExist()) {
			this.popup.setOpening(true);
			this.popup.getWindow().title = "Save Options";
			this.popup.getWindow().setPos(this.width / 2 - 210 / 2, this.height / 2 - 100 / 2);
			this.popupField = this.popup;
			this.popupField.getWindow().clearChildren();
			this.popupField.getWindow().addChild(new TextSegment(this, 5, 30, 20, 20, "The default options already exist\n\nWould you like to overwrite them?", 0, true));
			this.popupField.getWindow().addChild(new QuitButtonSegment(this, 190, 5, 14, 14, button -> {

				GuiConfig.this.popupField.setOpening(false);

				return true;
			}, true));

			this.popupField.getWindow().addChild(new ButtonSegment(this, 105 - 30, 75, "Overwrite", button -> {
				GuiConfig.this.cooldowns[0].setProgress(true);

				GuiConfig.this.popupField.setOpening(false);
				tpe.execute(new Runnable() {
					@Override
					public void run() {

						try {
							FileUtil.saveOptions();
							GuiConfig.this.cooldowns[0].renderCooldown = 30;
						} catch (ClosedByInterruptException e) {
							DefaultSettings.log.log(Level.DEBUG, "An exception occurred while saving the default game options: Interruption exception");
						} catch (Exception e) {
							DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the default game options:", e);
							GuiConfig.this.cooldowns[0].renderCooldown = -30;
						}
						GuiConfig.this.cooldowns[0].setProgress(false);
					}
				});
				return true;
			}, 60, 20, 2, null, true));

			this.popup.isVisible = true;
		} else {
			GuiConfig.this.cooldowns[0].setProgress(true);

			try {
				FileUtil.saveOptions();
				this.cooldowns[0].renderCooldown = 30;
			} catch (ClosedByInterruptException e) {
				DefaultSettings.log.log(Level.DEBUG, "An exception occurred while saving the default game options: Interruption exception");
			} catch (Exception e) {
				DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the default game options:", e);
				this.cooldowns[0].renderCooldown = -30;
			}
			this.cooldowns[0].setProgress(false);
		}
	}

    public void saveKeys() {
		if (FileUtil.keysFileExist()) {
			this.popup.setOpening(true);
			this.popup.getWindow().title = "Save Keybindings";
			this.popup.getWindow().setPos(this.width / 2 - 210 / 2, this.height / 2 - 100 / 2);
			this.popupField = this.popup;
			this.popupField.getWindow().clearChildren();
			this.popupField.getWindow().addChild(new TextSegment(this, 5, 30, 20, 20, "The default keybindings already exist\n\nWould you like to overwrite them?", 0, true));
			this.popupField.getWindow().addChild(new QuitButtonSegment(this, 190, 5, 14, 14, button -> {

				GuiConfig.this.popupField.setOpening(false);

				return true;
			}, true));

			this.popupField.getWindow().addChild(new ButtonSegment(this, 105 - 30, 75, "Overwrite", button -> {
				GuiConfig.this.cooldowns[2].setProgress(true);

				GuiConfig.this.popupField.setOpening(false);
				tpe.execute(new Runnable() {
					@Override
					public void run() {

						try {
							FileUtil.saveKeys();
							GuiConfig.this.cooldowns[2].renderCooldown = 30;
							FileUtil.restoreKeys();
						} catch (ClosedByInterruptException e) {
							DefaultSettings.log.log(Level.DEBUG, "An exception occurred while saving the key configuration: Interruption exception");
						} catch (Exception e) {
							DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the key configuration:", e);
							GuiConfig.this.cooldowns[2].renderCooldown = -30;
						}
						GuiConfig.this.cooldowns[2].setProgress(false);
					}
				});
				return true;
			}, 60, 20, 2, null, true));

			this.popup.isVisible = true;

		} else {

			GuiConfig.this.cooldowns[2].setProgress(true);

			try {
				FileUtil.saveKeys();
				this.cooldowns[2].renderCooldown = 30;
				FileUtil.restoreKeys();
			} catch (ClosedByInterruptException e) {
				DefaultSettings.log.log(Level.DEBUG, "An exception occurred while saving the key configuration: Interruption exception");
			} catch (Exception e) {
				DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the key configuration:", e);
				this.cooldowns[2].renderCooldown = -30;
			}
			this.cooldowns[2].setProgress(false);
		}
	}
    
	private class ButtonState {

		private boolean inProgress = false;
		public int renderCooldown = 0;

		public ButtonState(boolean inProgress, int renderCooldown) {
			this.inProgress = inProgress;
			this.renderCooldown = renderCooldown;
		}

		public void setProgress(boolean inProgress) {
			this.inProgress = inProgress;
		}

		public boolean getProgress() {
			return this.inProgress;
		}

	}
	
	public static int clamp(int num, int min, int max)
    {
        if (num < min)
        {
            return min;
        }
        else
        {
            return num > max ? max : num;
        }
    }
}