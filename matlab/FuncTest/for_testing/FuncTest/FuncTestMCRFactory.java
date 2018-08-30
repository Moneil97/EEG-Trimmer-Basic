/*
 * MATLAB Compiler: 6.6 (R2018a)
 * Date: Wed Aug 29 23:46:27 2018
 * Arguments: "-B""macro_default""-W""java:FuncTest,Class1""-T""link:lib""-d""F:\\Cloud 
 * Storages\\Google Drive\\Desktop\\Test1\\FuncTest\\for_testing""class{Class1:F:\\Cloud 
 * Storages\\Google Drive\\Desktop\\Test1\\FuncTest.m}"
 */

package FuncTest;

import com.mathworks.toolbox.javabuilder.*;
import com.mathworks.toolbox.javabuilder.internal.*;

/**
 * <i>INTERNAL USE ONLY</i>
 */
public class FuncTestMCRFactory
{
   
    
    /** Component's uuid */
    private static final String sComponentId = "FuncTest_A8B732283DC3CC863BEE251AF6F9C268";
    
    /** Component name */
    private static final String sComponentName = "FuncTest";
    
   
    /** Pointer to default component options */
    private static final MWComponentOptions sDefaultComponentOptions = 
        new MWComponentOptions(
            MWCtfExtractLocation.EXTRACT_TO_CACHE, 
            new MWCtfClassLoaderSource(FuncTestMCRFactory.class)
        );
    
    
    private FuncTestMCRFactory()
    {
        // Never called.
    }
    
    public static MWMCR newInstance(MWComponentOptions componentOptions) throws MWException
    {
        if (null == componentOptions.getCtfSource()) {
            componentOptions = new MWComponentOptions(componentOptions);
            componentOptions.setCtfSource(sDefaultComponentOptions.getCtfSource());
        }
        return MWMCR.newInstance(
            componentOptions, 
            FuncTestMCRFactory.class, 
            sComponentName, 
            sComponentId,
            new int[]{9,4,0}
        );
    }
    
    public static MWMCR newInstance() throws MWException
    {
        return newInstance(sDefaultComponentOptions);
    }
}
