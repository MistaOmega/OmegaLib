package mistaomega.lib.datagen.base.models;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;

import javax.annotation.Nonnull;

/**
 * Use for file generation
 */
public class BaseItemModelProvider extends ItemModelProvider
{

    public BaseItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper)
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
        return "Item Model Provider " + modid;
    }

    private ResourceLocation itemTexResourceLoc(IItemProvider itemProvider)
    {
        return modLoc("item/" + itemProvider.asItem().getName().toString());
    }

    protected void registerGenerated(IItemProvider... itemProviders)
    {
        for (IItemProvider itemProvider : itemProviders)
        {
            Generated(itemProvider);
        }
    }

    protected ItemModelBuilder Generated(IItemProvider itemProvider)
    {
        return Generated(itemProvider, itemTexResourceLoc(itemProvider));
    }

    protected ItemModelBuilder Generated(IItemProvider itemProvider, ResourceLocation texture)
    {
        return getBuilder(itemProvider.asItem().getName().toString()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", texture);
    }

    protected void registerHandheld(IItemProvider... itemProviders)
    {
        for (IItemProvider itemProvider : itemProviders)
        {
            Handheld(itemProvider);
        }
    }

    protected ItemModelBuilder Handheld(IItemProvider itemProvider)
    {
        return Handheld(itemProvider, itemTexResourceLoc(itemProvider));
    }

    protected ItemModelBuilder Handheld(IItemProvider itemProvider, ResourceLocation texture)
    {
        return withExistingParent(itemProvider.asItem().getName().toString(), "item/handheld").texture("layer0", texture);
    }


}
