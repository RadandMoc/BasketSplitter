# Ocado Project README

This project was completed for Ocado within 1.5 days.

## Project Structure

- The main class for this project is `BasketSplitter`, located at `src/main/java/com/ocado/basket/BasketSplitter.java`.
- Test cases can be found in the test class `BasketSplitterTest`, located at `src/test/java/com/ocado/basket/BasketSplitterTest.java`.
- The fat jar for this project is located at `BasketSplitter\build\libs\BasketSplitter-1.0-all.jar`

## Test Configuration

To execute the tests correctly, ensure to update the absolute path to the configuration file in the test class. Look for the variable `absolutePathToConfigFile` on line 17 of the test class.

## BasketSplitter Class

The `BasketSplitter` class exposes two public variables to optimize memory usage:
- `Map<String, List<Byte>> allProductsDictionary`: a dictionary where keys represent product names and values represent the IDs of possible delivery types.
- `Map<Byte, String> dynamicEnumOfDelivery`: a dictionary containing names of possible delivery types, where keys represent IDs.

The constructor of `BasketSplitter` requires a string representing the absolute path to the JSON file to initialize its variables.

The `BasketSplitter` class provides a single public function named `split`, which divides the customer's basket into a map. The keys of this map represent delivery types, while the values contain the names of products that can be delivered. Note that this function may take longer to execute if the configuration file contains a large number of delivery types.

## Jars
In the build.gradle.kts is task to make fatJar named "fatJar". Addictionally you can make "light jar" running task "shadowJar2"
