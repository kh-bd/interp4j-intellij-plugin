package dev.khbd.interp4j.intellij.usage;

import com.intellij.codeInsight.daemon.ImplicitUsageProvider;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.psi.search.PsiSearchHelper.SearchCostResult.FEW_OCCURRENCES;

/**
 * @author Sergei_Khadanovich
 */
public class InStringLiteralsImplicitUsageProvider implements ImplicitUsageProvider {

    private static @Nullable SearchScope getCheapSearchScope(@NotNull PsiElement field) {
        SearchScope scope = field.getUseScope();
        if (scope instanceof LocalSearchScope) {
            return scope;
        }

        String name;
        if (field instanceof PsiNamedElement) {
            name = ((PsiNamedElement) field).getName();
        } else {
            name = field.getText();
        }

        Project project = field.getProject();
        PsiSearchHelper searchHelper = PsiSearchHelper.getInstance(project);

        if (scope instanceof GlobalSearchScope &&
                searchHelper.isCheapEnoughToSearch(name, (GlobalSearchScope) scope, null) == FEW_OCCURRENCES) {
            return scope;
        }
        return null;
    }

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
            SearchScope scope = getCheapSearchScope(element);
            if (scope == null) {
                return false;
            }

            Query<PsiReference> usageQuery = ReferencesSearch.search(element, scope);
            return usageQuery.findFirst() != null;
        }
    }
}
