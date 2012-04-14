package org.Zeitline.Plugin.Input;

import org.Zeitline.GUI.FormGenerator;
import org.Zeitline.GUI.IFormGenerator;
import org.Zeitline.GUI.IFormItem;
import org.Zeitline.Plugin.IClassInstantiator;
import org.Zeitline.Plugin.PluginLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class InputPluginLoader extends PluginLoader<InputFilter> {

    public InputPluginLoader(String folderName) {
        super(folderName);
        FormGenerator formGenerator = new FormGenerator();
        InputFilterInstantiator instantiator = new InputFilterInstantiator(formGenerator);
        super.setInstantiator(instantiator);
    }



    public class InputFilterInstantiator implements IClassInstantiator<InputFilter> {
        private final IFormGenerator formGenerator;

        public InputFilterInstantiator(IFormGenerator formGenerator){
            this.formGenerator = formGenerator;
        }

        @Override public InputFilter newInstance(Class classDef) throws InstantiationException, IllegalAccessException, ClassCastException {
            Class[] constructorArgs = { IFormGenerator.class };
            Object[] constructorParams = { formGenerator };
            Constructor constructor;
            InputFilter classInst = null;

            try {
                constructor = classDef.getConstructor(constructorArgs);
                classInst = (InputFilter) constructor.newInstance(constructorParams);
            } catch (NoSuchMethodException nsm) {
                System.err.println(nsm);
            } catch (InvocationTargetException it) {
                System.err.println(it);
            }

            return classInst;
        }
    }
}
