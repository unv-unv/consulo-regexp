/*
 * Copyright 2006 Sascha Weinreuter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.intellij.lang.regexp.validation;

import javax.annotation.Nonnull;

import org.intellij.lang.regexp.RegExpTT;
import org.intellij.lang.regexp.psi.RegExpChar;
import org.intellij.lang.regexp.psi.impl.RegExpElementImpl;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;

class RemoveRedundantEscapeAction implements IntentionAction {
    private final RegExpChar myChar;

    public RemoveRedundantEscapeAction(RegExpChar ch) {
        myChar = ch;
    }

    @Nonnull
    public String getText() {
        return "Remove Redundant Escape";
    }

    @Nonnull
    public String getFamilyName() {
        return "Redundant Character Escape";
    }

    public boolean isAvailable(@Nonnull Project project, Editor editor, PsiFile file) {
        return myChar.isValid() && myChar.getUnescapedText().startsWith("\\");
    }

    public void invoke(@Nonnull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        final Character v = myChar.getValue();
        assert v != null;

        final ASTNode node = myChar.getNode().getFirstChildNode();
        final ASTNode parent = node.getTreeParent();
        parent.addLeaf(RegExpTT.CHARACTER, replacement(v), node);
        parent.removeChild(node);
    }

    private String replacement(Character v) {
        final PsiElement context = myChar.getContainingFile().getContext();
        return RegExpElementImpl.isLiteralExpression(context) ?
                StringUtil.escapeStringCharacters(v.toString()) :
                /*(context instanceof XmlElement ?
                        XmlStringUtil.escapeString(v.toString()) : */
                        v.toString()/*)*/;
    }

  public boolean startInWriteAction() {
        return true;
    }
}
