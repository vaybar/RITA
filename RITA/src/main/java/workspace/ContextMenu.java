package workspace;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import renderable.BlockUtilities;
import renderable.RenderableBlock;
import rita.settings.Language;
import codeblocks.Block;
import codeblocks.BlockStub;

/**
 * ContextMenu handles all the right-click menus within the Workspace. TODO ria
 * enable customization of what menu items appear, fire events depending on what
 * items are clicked (if we enabled the first feature)
 * 
 * TODO ria still haven't enabled the right click menu for blocks
 */
public class ContextMenu extends PopupMenu implements ActionListener {
	private static final long serialVersionUID = 328149080421L;
	// context menu renderableblocks plus
	// menu items for renderableblock context menu
	/*
	 * private final static String ADD_COMMENT = "addComment"; private final
	 * static String REMOVE_COMMENT = "removeComment"; private final static
	 * String CREATE_CALL = "createCall"; private final static String
	 * CREATE_SETTER = "setValue"; private final static String CREATE_GETTER =
	 * "getValue";
	 */
	private final static String CREATE_EVENT_METHOD = "eventMethod";

	private static ContextMenu rndBlockMenu = new ContextMenu();
	private static ContextMenu addCommentMenu = new ContextMenu();
	private static MenuItem addCommentItem;
	private final static String ADD_COMMENT_BLOCK = "ADDCOMMENT";
	private static boolean addCommentMenuInit = false;
	private static ContextMenu removeCommentMenu = new ContextMenu();
	private static MenuItem removeCommentItem;
	private final static String REMOVE_COMMENT_BLOCK = "REMOVECOMMENT";
	private static boolean removeCommentMenuInit = false;

	// private static boolean addCreateCallMenuInit = false;
	private static ContextMenu createCallMenu = new ContextMenu();
	private static MenuItem createCallItem;
	private final static String CREATE_CALL_BLOCK = "CREATECALL";
	// private static boolean createCallMenuInit = false;

	// private static boolean addCreateSetterMenuInit = false;
	private static ContextMenu createSetterMenu = new ContextMenu();
	private static MenuItem createSetterItem;
	private final static String CREATE_SETTER_BLOCK = "CREATESETTER";
	// private static boolean createSetterMenuInit = false;

	// private static boolean addCreateGetterMenuInit = false;
	private static ContextMenu createGetterMenu = new ContextMenu();
	private static MenuItem createGetterItem;
	private final static String CREATE_GETTER_BLOCK = "CREATEGETTER";

	private static ContextMenu createGetterParamMenu = new ContextMenu();
	private static MenuItem createGetterParamItem;
	private final static String CREATE_GETTER_PARAM_BLOCK = "CREATEGETTERPARAM";

	private static List<MenuItem> createEventMethodParamItem;

	// context menu for canvas plus
	// menu items for canvas context menu
	private static ContextMenu canvasMenu = new ContextMenu();
	private static MenuItem arrangeAllBlocks;
	private final static String ARRANGE_ALL_BLOCKS = "ARRANGE_ALL_BLOCKS";
	private final static String GENERATE_JAVA_CODE = "GENERATE_JAVA_CODE";
	private static boolean canvasMenuInit = false;
	/** The JComponent that launched the context menu in the first place */
	private static Object activeComponent = null;

	// privatize the constructor
	private ContextMenu() {
	}

	/**
	 * Initializes the context menu for adding Comments.
	 */
	private static void initAddCommentMenu() {
		addCommentItem = new MenuItem(Language.get("addComment"));
		addCommentItem.setActionCommand(ADD_COMMENT_BLOCK);
		addCommentItem.addActionListener(rndBlockMenu);
		addCommentMenu.add(addCommentItem);
		addCommentMenuInit = true;
	}

	/**
	 * Initializes the context menu for adding Comments.
	 */
	private MenuItem addItem(String name) {
		MenuItem item = new MenuItem(Language.get(name));
		item.setActionCommand(name);
		item.addActionListener(this);
		add(item);
		return item;
	}

	/**
	 * Initializes the context menu for deleting Comments.
	 */
	private static void initRemoveCommentMenu() {

		removeCommentItem = new MenuItem(Language.get("deleteComment"));
		removeCommentItem.setActionCommand(REMOVE_COMMENT_BLOCK);
		removeCommentItem.addActionListener(rndBlockMenu);

		removeCommentMenu.add(removeCommentItem);

		removeCommentMenuInit = true;
	}

	/**
	 * Initializes the context menu for the BlockCanvas
	 * 
	 */
	private static void initCanvasMenu() {
		arrangeAllBlocks = new MenuItem(Language.get("arrangeBlocks")); // TODO
																		// some
																		// workspaces
																		// don't
																		// have
																		// pages
		arrangeAllBlocks.setActionCommand(ARRANGE_ALL_BLOCKS);
		arrangeAllBlocks.addActionListener(canvasMenu);

		canvasMenu.add(arrangeAllBlocks);

		canvasMenuInit = true;
	}

	/**
	 * Returns the right click context menu for the specified JComponent. If
	 * there is none, returns null.
	 * 
	 * @param o
	 *            JComponent object seeking context menu
	 * @return the right click context menu for the specified JComponent. If
	 *         there is none, returns null.
	 */
	public static PopupMenu getContextMenuFor(Object o) {

		if (o instanceof RenderableBlock) {

			ContextMenu menu = new ContextMenu();

			// Si el elemento tiene comentarios agrego RemoveCommentItem sino
			// agrego AddComentItem
			if (((RenderableBlock) o).hasComment()) {
				// <<<<<<< .working
				if (!removeCommentMenuInit) {
					initRemoveCommentMenu();
				}
				activeComponent = o;
				initCreateCallMenu(o, removeCommentMenu);
				initCreateSetterMenu(o, removeCommentMenu);
				initCreateGetterMenu(o, removeCommentMenu);
				initCreateGetterParamMenu(o, removeCommentMenu);
				initCreateEventMethodParamMenu(o, removeCommentMenu);
				return removeCommentMenu;

			} else {

				if (!addCommentMenuInit) {
					initAddCommentMenu();
				}
				activeComponent = o;
				initCreateCallMenu(o, addCommentMenu);
				initCreateSetterMenu(o, addCommentMenu);
				initCreateGetterMenu(o, addCommentMenu);
				initCreateGetterParamMenu(o, addCommentMenu);
				initCreateEventMethodParamMenu(o, addCommentMenu);
				return addCommentMenu;
			}
		} else if (o instanceof BlockCanvas) {
			activeComponent = o;
			initCanvasMenu();
			return canvasMenu;
		}
		return null;
	}

	private static void initCreateCallMenu(Object o, ContextMenu commentMenu) {
		if (((RenderableBlock) o).getBlock().isProcedureDeclBlock()) {
			if (createCallItem == null) {
				createCallItem = new MenuItem(Language.get("createCall"));
				createCallItem.setActionCommand(CREATE_CALL_BLOCK);
				createCallItem.addActionListener(createCallMenu);
			}
			commentMenu.add(createCallItem);

		} else {
			// if the Context Menu already contains "Create Call" and it has to
			// be removed
			commentMenu.remove(createCallItem);
		}

	}

	private static void initCreateEventMethodParamMenu(Object o,
			ContextMenu commentMenu) {
		if (createEventMethodParamItem!=null){
			for (MenuItem menuItem : createEventMethodParamItem)
				commentMenu.remove(menuItem);
			createEventMethodParamItem = null;
		}
		
		Block block=((RenderableBlock)o).getBlock(); 
		if (block.isProcedureParamBlock() && block.isEvent()) {
				createEventMethodParamItem = new ArrayList<MenuItem>();
				ArrayList<BlockStub> stubList = (ArrayList<BlockStub>) ((RenderableBlock) o)
						.getBlock().getFreshStubs();
				if (stubList != null) {
					for (BlockStub stub : stubList) {
						if (stub.isEventMethod()) {
							createEventMethodParamItem.add(commentMenu
									.addItem(CREATE_EVENT_METHOD
											+ stub.getProperty("name")));
						}
					}
				}

			}
//		}

	}

	private static void initCreateSetterMenu(Object o, ContextMenu commentMenu) {
		if (((RenderableBlock) o).getBlock().isVariableDeclBlock()
				&& ((RenderableBlock) o).getBlock().getSocketAt(0).getBlockID() != Block.NULL) {
			if (createSetterItem == null) {
				createSetterItem = new MenuItem(Language.get("setValue"));
				createSetterItem.setActionCommand(CREATE_SETTER_BLOCK);
				createSetterItem.addActionListener(createSetterMenu);
			}
			commentMenu.add(createSetterItem);

		} else {
			commentMenu.remove(createSetterItem);
		}

	}

	private static void initCreateGetterMenu(Object o, ContextMenu commentMenu) {

		if ((((RenderableBlock) o).getBlock().isVariableDeclBlock() && ((RenderableBlock) o)
				.getBlock().getSocketAt(0).getBlockID() != Block.NULL)) {
			if (createGetterItem == null) {
				createGetterItem = new MenuItem(Language.get("getValue"));
				createGetterItem.setActionCommand(CREATE_GETTER_BLOCK);
				createGetterItem.addActionListener(createGetterMenu);
			}
			commentMenu.add(createGetterItem);

		} else {
			// if the Context Menu already contains "Create Call" and it has to
			// be removed
			commentMenu.remove(createGetterItem);
		}

	}

	private static void initCreateGetterParamMenu(Object o,
			ContextMenu commentMenu) {

		Block block=((RenderableBlock) o).getBlock();
		if (block.isProcedureParamBlock() && !block.isEvent()) {
			if (createGetterParamItem == null) {
				createGetterParamItem = new MenuItem(
						Language.get("getValueParam"));
				createGetterParamItem
						.setActionCommand(CREATE_GETTER_PARAM_BLOCK);
				createGetterParamItem.addActionListener(createGetterParamMenu);
			}
			commentMenu.add(createGetterParamItem);

		} else {
			commentMenu.remove(createGetterParamItem);
		}

	}

	public void actionPerformed(ActionEvent a) {
		if (a.getActionCommand() == ARRANGE_ALL_BLOCKS) {
			// notify the component that launched the context menu in the first
			// place
			if (activeComponent != null
					&& activeComponent instanceof BlockCanvas) {
				((BlockCanvas) activeComponent).arrangeAllBlocks();
			}
		} else if (a.getActionCommand() == ADD_COMMENT_BLOCK) {
			// notify the renderableblock componenet that lauched the conetxt
			// menu
			if (activeComponent != null
					&& activeComponent instanceof RenderableBlock) {
				((RenderableBlock) activeComponent).addComment();
			}
		} else if (a.getActionCommand() == REMOVE_COMMENT_BLOCK) {
			// notify the renderableblock componenet that lauched the conetxt
			// menu
			if (activeComponent != null
					&& activeComponent instanceof RenderableBlock) {
				((RenderableBlock) activeComponent).removeComment();
			}
		}
		if (a.getActionCommand() == GENERATE_JAVA_CODE) {
			if (activeComponent != null
					&& activeComponent instanceof BlockCanvas) {
				((BlockCanvas) activeComponent).generateJavaCode();
			}
		}

		if (a.getActionCommand() == CREATE_CALL_BLOCK) {
			if (activeComponent != null
					&& activeComponent instanceof RenderableBlock) {
				RenderableBlock newRB = BlockUtilities
						.getCaller((RenderableBlock) activeComponent,
								Workspace.getPage(0));
				Workspace.getPage(0).addBlock(newRB);
			}
		}

		if (a.getActionCommand() == CREATE_SETTER_BLOCK) {
			// notify the component that launched the context menu in the first
			// place
			if (activeComponent != null
					&& activeComponent instanceof RenderableBlock) {
				// WorkspaceController
				// .createSetter((RenderableBlock) activeComponent);
				// ((RenderableBlock)activeComponent).createCall();

				RenderableBlock newRB = BlockUtilities
						.getSetter((RenderableBlock) activeComponent);
				Workspace.getPage(0).addBlock(newRB);

			}
		}
		if (a.getActionCommand() == CREATE_GETTER_BLOCK) {
			// notify the component that launched the context menu in the first
			// place
			if (activeComponent != null
					&& activeComponent instanceof RenderableBlock) {

				RenderableBlock newRB = BlockUtilities
						.getGetter((RenderableBlock) activeComponent);
				Workspace.getPage(0).addBlock(newRB);

			}
		}
		if (a.getActionCommand() == CREATE_GETTER_PARAM_BLOCK) {
			// notify the component that launched the context menu in the first
			// place
			if (activeComponent != null
					&& activeComponent instanceof RenderableBlock) {

				RenderableBlock newRB = BlockUtilities
						.getGetter((RenderableBlock) activeComponent);
				Workspace.getPage(0).addBlock(newRB);

			}
		}
		
		if(a.getActionCommand().contains(CREATE_EVENT_METHOD)) {
			String name = a.getActionCommand().substring(CREATE_EVENT_METHOD.length());
			RenderableBlock newRB = BlockUtilities.getEventMethod((RenderableBlock) activeComponent, name);
			Workspace.getPage(0).addBlock(newRB);
		}

	}

}