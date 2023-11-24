package os;

public interface SystemCall {


    String mkdir(String name);

    String rmdir(String name);

    String ls();

    String touch(String name);

    String rm(String name);

    String cd(String name);

    String save();
}
