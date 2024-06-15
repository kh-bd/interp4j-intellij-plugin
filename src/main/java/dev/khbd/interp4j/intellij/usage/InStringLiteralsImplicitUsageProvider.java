package dev.khbd.interp4j.intellij.usage;

import com.intellij.codeInsight.daemon.ImplicitUsageProvider;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;

/**
 * @author Sergei_Khadanovich
 */
public class InStringLiteralsImplicitUsageProvider implements ImplicitUsageProvider {

    @Override
    public boolean isImplicitUsage(@NotNull PsiElement element) {
        return ProgressManager.getInstance().runProcess(new IsUsedImplicitly(element), new EmptyProgressIndicator());
    }

    @Override
    public boolean isImplicitWrite(@NotNull PsiElement element) {
        return false;
    }

    @Override
    public boolean isImplicitRead(@NotNull PsiElement element) {
        return false;
    }

    private record IsUsedImplicitly(PsiElement element) implements Computable<Boolean> {

        @Override
        public Boolean compute() {
            SearchScope scope = element.getUseScope();
            Query<PsiReference> usageQuery = ReferencesSearch.search(element, scope);
            return usageQuery.findFirst() != null;
        }
    }
}
