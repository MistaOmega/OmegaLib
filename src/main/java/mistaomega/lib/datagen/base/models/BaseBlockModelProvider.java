package mistaomega.lib.datagen.base.models;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

import javax.annotation.Nonnull;

public class BaseBlockModelProvider extends BlockModelProvider
{
    public BaseBlockModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper)
    {
        super(generator, modid, existingFileHelper);
    }

    @Override
    protected void registerModels()
    {

    }

    @Nonnull
    @Override
    public String getName()
    {
        return "Block model provider: " + modid;
    }

    public BlockModelBuilder frameBuilder(String name, ResourceLocation parent, ResourceLocation tex)
    {
        return withExistingParent(name, parent)
                .texture("side", tex)
                .texture("top", tex)
                .texture("bottom", tex);
    }
}
