package dev.khbd.interp4j.intellij.usage;

import com.intellij.codeInsight.daemon.ImplicitUsageProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiParameter;
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
        if (element instanceof PsiLocalVariable
                || element instanceof PsiParameter
                || element instanceof PsiField) {
            SearchScope scope = element.getUseScope();
            Query<PsiReference> usageQuery = ReferencesSearch.search(element, scope);
            return usageQuery.findFirst() != null;
        }
        return false;
    }

    @Override
    public boolean isImplicitWrite(@NotNull PsiElement element) {
        return false;
    }

    @Override
    public boolean isImplicitRead(@NotNull PsiElement element) {
        return false;
    }

}
