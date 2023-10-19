package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProgramCompiler {

    public static void main(String[] args) {
        List<String> inputs = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            inputs.add(input);
            if (input.equals("00000000000000000000000000000000")) break;
        }
        for (String input : inputs)
            System.out.println(Long.parseLong(input, 2));
        System.out.println("Size : " + inputs.size());
    }

}
