import domain.User;
import repository.FileUserRepository;
import repository.Repository;
import service.SocialNetworkService;
import validator.UserValidator;
import exceptions.ValidationException;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static SocialNetworkService service;

    public static void main(String[] args) {
        Repository<String, User> repository = new FileUserRepository("src/main/resources/users.txt", new UserValidator());
        service = new SocialNetworkService(repository);

        while (true) {
            printMenu();
            String option = scanner.nextLine();
            handleOption(option);
        }
    }

    private static void printMenu() {
        System.out.println("\n=== Social Network Menu ===");
        System.out.println("1. Add user");
        System.out.println("2. Remove user");
        System.out.println("3. Add friendship");
        System.out.println("4. Remove friendship");
        System.out.println("5. Show number of communities");
        System.out.println("6. Show most sociable community");
        System.out.println("7. Exit");
        System.out.print("Choose an option: ");
    }

    private static void handleOption(String option) {
        try {
            switch (option) {
                case "0" -> debug();
                case "1" -> addUser();
                case "2" -> removeUser();
                case "3" -> addFriendship();
                case "4" -> removeFriendship();
                case "5" -> showNumberOfCommunities();
                case "6" -> showMostSociableCommunity();
                case "7" -> {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid option!");
            }
        } catch (ValidationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void debug() {
        for (User user : service.getAllUsers()) {
            System.out.println(user);
        }
    }

    private static void addUser() throws ValidationException {
        System.out.print("Enter user ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();

        service.addUser(new User(id, firstName, lastName));
        System.out.println("User added successfully!");
    }

    private static void removeUser() {
        System.out.print("Enter user ID to remove: ");
        String id = scanner.nextLine();
        service.removeUser(id);
        System.out.println("User removed successfully!");
    }

    private static void addFriendship() throws ValidationException {
        System.out.print("Enter first user ID: ");
        String id1 = scanner.nextLine();
        System.out.print("Enter second user ID: ");
        String id2 = scanner.nextLine();

        service.addFriendship(id1, id2);
        System.out.println("Friendship added successfully!");
    }

    private static void removeFriendship() {
        System.out.print("Enter first user ID: ");
        String id1 = scanner.nextLine();
        System.out.print("Enter second user ID: ");
        String id2 = scanner.nextLine();

        service.removeFriendship(id1, id2);
        System.out.println("Friendship removed successfully!");
    }

    private static void showNumberOfCommunities() {
        int count = service.getNumberOfCommunities();
        System.out.println("Number of communities: " + count);
    }

    private static void showMostSociableCommunity() {
        List<User> community = service.getMostSociableCommunity();
        System.out.println("Most sociable community members:");
        for (User user : community) {
            System.out.println(user);
        }
    }
}