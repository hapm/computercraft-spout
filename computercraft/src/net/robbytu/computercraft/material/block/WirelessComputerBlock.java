package net.robbytu.computercraft.material.block;

import net.robbytu.computercraft.material.Materials;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.block.design.GenericCubeBlockDesign;
import org.getspout.spoutapi.block.design.Texture;
import org.getspout.spoutapi.inventory.SpoutItemStack;

public class WirelessComputerBlock extends BaseComputerBlock {

	public WirelessComputerBlock(Plugin plugin, String name, boolean isOpaque, int face) {
		super(plugin, name, "http://robbytu.net/spout/computercraft/resources/computerblock.png", isOpaque, face);
		this.setName("Computer with Wireless Network Card");
		
		if (!name.equals("WirelessComputerBlockEast"))
			setItemDrop(new SpoutItemStack(Materials.WirelessComputerBlockEast, 1));
		
		setupDesign(plugin, face, "http://robbytu.net/spout/computercraft/resources/computerblock.png");
	}
	
	@Override
	protected void setupDesign(Plugin plugin, int face, String texture) {
		GenericCubeBlockDesign BlockDesign;
		
		if(face == 0) {
			BlockDesign = new GenericCubeBlockDesign(
					plugin,
					new Texture(plugin, texture, 256, 256, 16),
					new int[] { 2, 3, 3, 0, 3, 2 });
		}
		else if(face == 1) {
			BlockDesign = new GenericCubeBlockDesign(
					plugin,
					new Texture(plugin, texture, 256, 256, 16),
					new int[] { 2, 0, 3, 3, 3, 2 });
		}
		else if(face == 3) {
			BlockDesign = new GenericCubeBlockDesign(
					plugin,
					new Texture(plugin, texture, 256, 256, 16),
					new int[] { 2, 3, 3, 3, 0, 2 });
		}
		else {
			BlockDesign = new GenericCubeBlockDesign(
					plugin,
					new Texture(plugin, texture, 256, 256, 16),
					new int[] { 2, 3, 0, 3, 3, 2 });
		}
		
		this.setBlockDesign(BlockDesign);
	}
	
	@Override
	public void onBlockPlace(World world, int x, int y, int z) {
		onBlockPlace(world, x, y, z, true);
	}
}
