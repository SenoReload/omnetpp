package com.swtworkbench.community.xswt.codegens;

import com.swtworkbench.community.xswt.codegen.ICodeGenerator;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

public class FontCodeGenerator implements ICodeGenerator {
    public String getCode(Object o, String source) {
        Font font = (Font) o;
        FontData fd = font.getFontData()[0];
        return "new Font(Display.getCurrent(), \"" + fd.getName() + "\", " + fd.getHeight() + ", " + fd.getStyle() + ");";
    }
}