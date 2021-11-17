package dev.khbd.interp4j.intellij;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.LanguageLevelModuleExtension;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.PsiTestUtil;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.util.PathUtil;
import lombok.experimental.UtilityClass;

import java.io.File;

/**
 * @author Sergei_Khadanovich
 */
@UtilityClass
public class LensTestUtil {

    private static final String LIB_PATH = "build/lib";
    private static final String JDK_PATH = "JDK/JDK";

    /**
     * Create project descriptor.
     *
     * @param languageLeveL language level to set up
     * @return project descriptor
     */
    public static LightProjectDescriptor createProjectDescriptor(LanguageLevel languageLeveL) {
        return new ProjectDescriptor(languageLeveL);
    }

    /**
     * Load library into project
     *
     * @param disposable project disposable
     * @param module     module
     * @param libName    library name
     * @param jarName    library jar
     */
    public static void loadLibrary(Disposable disposable, Module module,
                                   String libName,
                                   String jarName) {
        String libPath = PathUtil.toSystemIndependentName(new File(LIB_PATH).getAbsolutePath());
        VfsRootAccess.allowRootAccess(disposable, libPath);
        PsiTestUtil.addLibrary(disposable, module, libName, libPath, jarName);
    }

    private static class ProjectDescriptor extends DefaultLightProjectDescriptor {

        private final LanguageLevel languageLeveL;

        public ProjectDescriptor(LanguageLevel languageLeveL) {
            this.languageLeveL = languageLeveL;
        }

        @Override
        public Sdk getSdk() {
            return JavaSdk.getInstance()
                    .createJdk(
                            languageLeveL.name(),
                            JDK_PATH + "-" + languageLeveL.toJavaVersion(),
                            false
                    );
        }

        @Override
        public void configureModule(Module module, ModifiableRootModel model, ContentEntry contentEntry) {
            model.getModuleExtension(LanguageLevelModuleExtension.class).setLanguageLevel(languageLeveL);
        }
    }
}
