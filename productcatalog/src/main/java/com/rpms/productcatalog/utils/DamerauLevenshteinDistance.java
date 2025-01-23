package com.rpms.productcatalog.utils;

public class DamerauLevenshteinDistance {

    /**
     * Calculate the Damerau-Levenshtein distance between two strings.
     *
     * @param source The source string.
     * @param target The target string.
     * @return The minimum number of operations required to transform source into target.
     */
    public int calculateDistance(String source, String target) {
        int sourceLength = source.length();
        int targetLength = target.length();

        // Initialize the distance matrix
        int[][] distanceMatrix = new int[sourceLength + 1][targetLength + 1];

        // Populate the base cases for transformations involving empty strings
        for (int i = 0; i <= sourceLength; i++) {
            distanceMatrix[i][0] = i;
        }
        for (int j = 0; j <= targetLength; j++) {
            distanceMatrix[0][j] = j;
        }

        // Compute the distance matrix
        for (int i = 1; i <= sourceLength; i++) {
            for (int j = 1; j <= targetLength; j++) {
                char sourceChar = source.charAt(i - 1);
                char targetChar = target.charAt(j - 1);

                // Cost of substitution
                int substitutionCost = (sourceChar == targetChar) ? 0 : 1;

                distanceMatrix[i][j] = Math.min(
                        Math.min(
                                distanceMatrix[i - 1][j] + 1, // Deletion
                                distanceMatrix[i][j - 1] + 1  // Insertion
                        ),
                        distanceMatrix[i - 1][j - 1] + substitutionCost // Substitution
                );

                // Check for transposition (swap of adjacent characters)
                if (i > 1 && j > 1 &&
                        sourceChar == target.charAt(j - 2) &&
                        source.charAt(i - 2) == targetChar) {
                    distanceMatrix[i][j] = Math.min(
                            distanceMatrix[i][j],
                            distanceMatrix[i - 2][j - 2] + 1 // Transposition
                    );
                }
            }
        }

        // The final distance is in the bottom-right corner of the matrix
        return distanceMatrix[sourceLength][targetLength];
    }
}
