package os.process_manager;

import os.memory_manager.Page;

public class Process {

    private Context context;
    private Page codeSegment;
    private Page dataSegment;

    public Process(Context context, Page codeSegment, Page dataSegment) {
        this.context = context;
        this.codeSegment = codeSegment;
        this.dataSegment = dataSegment;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Page getCodeSegment() {
        return codeSegment;
    }

    public Page getDataSegment() {
        return dataSegment;
    }

}
