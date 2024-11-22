import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class DestinationHashGenerator {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: java DestinationHashGenerator <roll_number> <json_file_path>");
            return;
        }

        String rollNumber = args[0].toLowerCase();
        String filePath = args[1];

        // Parse JSON file
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(new File(filePath));

        // Find the first "destination" key
        String destinationValue = findDestinationValue(rootNode);
        if (destinationValue == null) {
            System.out.println("Key 'destination' not found in JSON.");
            return;
        }

        // Generate a random 8-character alphanumeric string
        String randomString = generateRandomString();

        // Concatenate rollNumber, destinationValue, and randomString
        String concatenated = rollNumber + destinationValue + randomString;

        // Generate MD5 hash
        String md5Hash = generateMD5Hash(concatenated);

        // Print result
        System.out.println(md5Hash + ";" + randomString);
    }

    // Function to find "destination" key recursively
    private static String findDestinationValue(JsonNode node) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (field.getKey().equals("destination")) {
                    return field.getValue().asText();
                }
                String result = findDestinationValue(field.getValue());
                if (result != null) {
                    return result;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode element : node) {
                String result = findDestinationValue(element);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    // Function to generate a random 8-character alphanumeric string
    private static String generateRandomString() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    // Function to generate MD5 hash
    private static String generateMD5Hash(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
