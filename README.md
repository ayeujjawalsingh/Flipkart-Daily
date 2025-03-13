# Flipkart Daily

## Description
Flipkart wants to build a product to deliver groceries and daily essentials by next morning. In the initial release, we want to build a browsing feature for Users where they can search for items from inventory using some filters and sorting criteria. This will give them an idea of how rich the inventory is before they go ahead and place an order.

## Features
- Define items in the inventory along with the price.
- Category & brand define the item. (e.g., category → milk, bread / brand → Amul milk or Britannia bread)
- **AddItem(category, brand, price)**: Add items to inventory with available quantity for each of them.
- **AddInventory(category, brand, quantity)**: Add inventory quantity for existing items.
- **Search items** using one of the filters like:
  - Brand or Category (search can be on multiple categories/brands)
  - Price range (price From, price To) (from and to price parameters can be optional)
- **Sorting of search results**:
  - Items with the lowest price first (default)
  - Items with the highest price first
  - Items with the least number of quantity
  - More sorting options may be added in the future.

### Bonus Features
1. Users should be able to search items in inventory using multiple filter criteria, such as:
   - Search by both category and brand
   - Search by both category and price range
   - Search by both brand and price range

## Other Details
- Write a driver class for demo purposes, which will execute all the commands at one place in the code and include test cases.
- Do **not** use any database or NoSQL store, use an **in-memory data-structure**.
- Do **not** create any UI for the application.
- Prioritize **code compilation, execution, and completion**.
- Work on the expected output first and then add good-to-have features of your own.

## Expectations
- Ensure the code is **working and demonstrable**.
- Code must be **functionally correct**.
- Proper use of **abstraction, modeling, and separation of concerns** is required.
- Code should be **modular, readable, and unit-testable**.
- The system should **easily accommodate new requirements** with minimal changes.
- **Proper exception handling** is required.

## Test Cases & Sample Execution

### Commands
```plaintext
AddItem(Amul, Milk, 100)
AddItem(Amul, Curd, 50)
AddItem(Nestle, Milk, 60)
AddItem(Nestle, Curd, 90)
AddInventory(Amul, Milk, 10)
AddInventory(Nestle, Milk, 5)
AddInventory(Nestle, Curd, 10)
AddInventory(Amul, Milk, 10)
AddInventory(Amul, Curd, 5)
```

### Inventory State
```plaintext
Amul -> Milk -> 20
Amul -> Curd -> 5
Nestle -> Milk -> 15
Nestle -> Curd -> 10
```

### Search Queries & Results
#### `SearchItems(brand=[Nestle])`
```plaintext
Nestle, Milk, 5
Nestle, Curd, 10
```

#### `SearchItems(category=[Milk])`
```plaintext
Nestle, Milk, 5
Amul, Milk, 20
```

#### `SearchItems(category=[Milk], Order_By=[Price,desc])`
```plaintext
Amul, Milk, 20
Nestle, Milk, 5
```

#### `SearchItems(price=[70, 100])`
```plaintext
Nestle, Curd, 10
Amul, Milk, 20
```

#### `SearchItems([category=[Milk], price=[70, 100]], Order_By=[Price,desc])`
```plaintext
Amul, Milk, 20
```

## Evaluation Criteria

### Must Have:
- `AddItem`
- `AddInventory`
- Search Item with default sorting criteria
- Extensibility
- Demonstrability
- Correct entity definitions
- Presence of a service layer
- Edge case validations

### Good to Have:
- More extensible sorting criteria
- Search with multiple criteria
- DAO layer
- Exception handling

## Code Implementation
```java
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

        // Adding -veytr inventory
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
```

## Sample Output
```plaintext
Error: Price must be positive.
Error: Quantity must be positive.

Search by brand Nestle:
Brand: Nestle, category: Milk, Price: 60, Quantity: 15
Brand: Nestle, category: Curd, Price: 90, Quantity: 10

Search by category Milk:
Brand: Nestle, category: Milk, Price: 60, Quantity: 15
Brand: Amul, category: Milk, Price: 100, Quantity: 20

Search by category Milk ordered by price descending:
Brand: Amul, category: Milk, Price: 100, Quantity: 20
Brand: Nestle, category: Milk, Price: 60, Quantity: 15

Search by price range 70 to 100:
Brand: Nestle, category: Curd, Price: 90, Quantity: 10
Brand: Amul, category: Milk, Price: 100, Quantity: 20

Search non-existent category:

```
