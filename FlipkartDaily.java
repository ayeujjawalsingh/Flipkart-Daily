import java.util.*;

class Item {
    String category;
    String brand;
    int price;
    int quantity;

    public Item(String category, String brand, int price, int quantity) {
        if (price <= 0 || quantity < 0) {
            throw new IllegalArgumentException("Price must be positive and quantity cannot be negative.");
        }
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Brand: " + brand + ", category: " + category + ", Price: " + price + ", Quantity: " + quantity;
    }
}

class InventoryManager {
    private final Map<String, Item> inventory = new HashMap<>();

    public void addItem(String category, String brand, int price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive.");
        }
        String key = category.toLowerCase() + ":" + brand.toLowerCase();
        inventory.putIfAbsent(key, new Item(category, brand, price, 0));
    }

    public void addInventory(String category, String brand, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        String key = category.toLowerCase() + ":" + brand.toLowerCase();
        Item item = inventory.get(key);
        if (item != null) {
            item.quantity += quantity;
        } else {
            throw new IllegalArgumentException("Item does not exist. Add item first.");
        }
    }

    public List<Item> searchItems(String category, String brand, Integer priceFrom, Integer priceTo, String orderBy, boolean ascending) {
        List<Item> result = new ArrayList<>(inventory.values());

        result.removeIf(item -> (category != null && !item.category.equalsIgnoreCase(category)));
        result.removeIf(item -> (brand != null && !item.brand.equalsIgnoreCase(brand)));
        result.removeIf(item -> (priceFrom != null && item.price < priceFrom));
        result.removeIf(item -> (priceTo != null && item.price > priceTo));

        Comparator<Item> comparator = Comparator.comparingInt(item -> item.price); // ascending order
        if ("quantity".equalsIgnoreCase(orderBy)) {
            comparator = Comparator.comparingInt(item -> item.quantity);
        }
        if (!ascending) {
            comparator = comparator.reversed();
        }
        result.sort(comparator);

        return result;
    }
}

public class Main {
    public static void main(String[] args) {
        InventoryManager manager = new InventoryManager();
        
        // Adding -ve price
        try {
            manager.addItem("Milk", "Amul", -10);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        manager.addItem("Milk", "Amul", 100);
        manager.addItem("Curd", "Amul", 50);
        manager.addItem("Milk", "Nestle", 60);
        manager.addItem("Curd", "Nestle", 90);

        // Adding -ve inventory
        try {
            manager.addInventory("Milk", "Amul", -5);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        manager.addInventory("Milk", "Amul", 20);
        manager.addInventory("Curd", "Amul", 5);
        manager.addInventory("Milk", "Nestle", 15);
        manager.addInventory("Curd", "Nestle", 10);

        System.out.println("\nSearch by brand Nestle:");
        manager.searchItems(null, "Nestle", null, null, "price", true).forEach(System.out::println);
        
        System.out.println("\nSearch by category Milk:");
        manager.searchItems("Milk", null, null, null, "price", true).forEach(System.out::println);
        
        System.out.println("\nSearch by category Milk ordered by price descending:");
        manager.searchItems("Milk", null, null, null, "price", false).forEach(System.out::println);
        
        System.out.println("\nSearch by price range 70 to 100:");
        manager.searchItems(null, null, 70, 100, "price", true).forEach(System.out::println);
        
        System.out.println("\nSearch non-existent category:");
        manager.searchItems("Bread", null, null, null, "price", true).forEach(System.out::println);
    }
}
