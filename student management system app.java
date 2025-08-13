import java.io.*;
import java.util.*;

// Abstract base class for shared attributes
abstract class Person implements Serializable {
    private int id;
    private String name;
    private int age;

    public Person(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }

    public abstract void displayDetails();
}

// Final Student class
final class Student extends Person {
    private String course;
    private static int studentCount = 0;

    public Student(int id, String name, int age, String course) {
        super(id, name, age);
        this.course = course;
        studentCount++;
    }

    @Override
    public void displayDetails() {
        System.out.println("ID     : " + getId());
        System.out.println("Name   : " + getName());
        System.out.println("Age    : " + getAge());
        System.out.println("Course : " + course);
    }

    public static int getStudentCount() {
        return studentCount;
    }

    public static void setStudentCount(int count) {
        studentCount = count;
    }

    public static void decrementCount() {
        studentCount--;
    }
}

// Manager class to handle students
class StudentManager {
    private List<Student> students = new ArrayList<>();
    private final String DATA_FILE = "students.dat";

    public StudentManager() {
        loadFromFile();
    }

    public void addStudent(Student student) {
        for (Student s : students) {
            if (s.getId() == student.getId()) {
                System.out.println("❌ Student with ID " + student.getId() + " already exists.");
                return;
            }
        }
        students.add(student);
        saveToFile();
        System.out.println("✅ Student added successfully.");
    }

    public void removeStudent(int id) {
        Iterator<Student> itr = students.iterator();
        while (itr.hasNext()) {
            Student s = itr.next();
            if (s.getId() == id) {
                itr.remove();
                Student.decrementCount();
                saveToFile();
                System.out.println("✅ Student removed successfully.");
                return;
            }
        }
        System.out.println("❌ No student found with ID: " + id);
    }

    public void displayAllStudents() {
        if (students.isEmpty()) {
            System.out.println("📂 No students to display.");
            return;
        }
        for (Student s : students) {
            System.out.println("---------------");
            s.displayDetails();
        }
        System.out.println("---------------");
        System.out.println("Total Students: " + Student.getStudentCount());
    }

    public void displayOneStudent(int id) {
        for (Student s : students) {
            if (s.getId() == id) {
                s.displayDetails();
                return;
            }
        }
        System.out.println("❌ No student found with ID: " + id);
    }

    // Save student list to file
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(students);
        } catch (IOException e) {
            System.out.println("⚠ Error saving data: " + e.getMessage());
        }
    }

    // Load student list from file
    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            students = (ArrayList<Student>) ois.readObject();
            // Restore static count
            Student.setStudentCount(students.size());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("⚠ Error loading data: " + e.getMessage());
        }
    }
}

// Main application class with menu
public class StudentManagementApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StudentManager manager = new StudentManager();
        int choice;

        do {
            System.out.println("\n--- Student Management Menu ---");
            System.out.println("1) Create Student");
            System.out.println("2) Remove Student");
            System.out.println("3) Display All Students");
            System.out.println("4) Display One Student");
            System.out.println("5) Exit");
            System.out.print("Enter your choice: ");

            while (!sc.hasNextInt()) {
                System.out.print("Please enter a valid number: ");
                sc.next();
            }
            choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter ID: ");
                    int id = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter Age: ");
                    int age = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Course: ");
                    String course = sc.nextLine();
                    Student student = new Student(id, name, age, course);
                    manager.addStudent(student);
                }
                case 2 -> {
                    System.out.print("Enter Student ID to remove: ");
                    int removeId = sc.nextInt();
                    manager.removeStudent(removeId);
                }
                case 3 -> manager.displayAllStudents();
                case 4 -> {
                    System.out.print("Enter Student ID to display: ");
                    int displayId = sc.nextInt();
                    manager.displayOneStudent(displayId);
                }
                case 5 -> System.out.println("👋 Exiting... Goodbye!");
                default -> System.out.println("❌ Invalid choice. Please try again.");
            }
        } while (choice != 5);

        sc.close();
    }
}
