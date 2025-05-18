package fungorium.utils;

import java.util.*;

import fungorium.gui.EntityController;
import fungorium.gui.TectonViewModel;
import fungorium.tectons.Tecton;

public class TectonPositioner {
    private EntityController entityController;
    private int occupiedPosition = 0;
    private Map<Tecton, double[]> assignedPositions = new HashMap<>();
    private Set<String> occupiedCoords = new HashSet<>(); // To track occupied (row, col) pairs
    private static final double[][] HEX_OFFSETS = {
            { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { -1, 1 } // Neighbor offsets in hexagonal grid
    };

    public TectonPositioner(EntityController entityController) {
        this.entityController = entityController;
    }

    public Map<Tecton, TectonViewModel> createTectonViewModels(Map<String, Object> objects, int rows, int cols,
            double hexWidth, double hexHeight) {
        Map<Tecton, TectonViewModel> tectonVMs = new HashMap<>();
        assignedPositions.clear();
        occupiedCoords.clear();
        occupiedPosition = 0;

        // Find all Tectons
        List<Tecton> tectons = new ArrayList<>();
        for (Map.Entry<String, Object> entry : objects.entrySet()) {
            if (entry.getValue() instanceof Tecton tecton) {
                tectons.add(tecton);
            }
        }

        if (tectons.isEmpty()) {
            return tectonVMs;
        }

        // Start with the first Tecton and assign positions using a queue-based approach
        Queue<Tecton> queue = new LinkedList<>();
        Set<Tecton> visited = new HashSet<>();
        queue.add(tectons.get(0));
        visited.add(tectons.get(0));

        // Assign the first Tecton to the origin (0, 0)
        assignPosition(tectons.get(0), 0, 0, hexWidth, hexHeight, tectonVMs);

        while (!queue.isEmpty()) {
            Tecton current = queue.poll();
            double[] currentPos = assignedPositions.get(current);
            int currentRow = (int) (currentPos[1] / (hexHeight + 10.0)); // Approximate row based on y
            int currentCol = (int) (currentPos[0] / (hexWidth + 10.0)); // Approximate col based on x

            // Get neighbors and try to place them in adjacent hexagonal positions
            List<Tecton> neighbors = current.getNeighbors();
            for (Tecton neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    // Find an unoccupied neighboring position
                    double[] newPos = findNeighborPosition(currentRow, currentCol, rows, cols, hexWidth, hexHeight);
                    if (newPos != null) {
                        assignPosition(neighbor, newPos[0], newPos[1], hexWidth, hexHeight, tectonVMs);
                        queue.add(neighbor);
                        visited.add(neighbor);
                    }
                }
            }
        }

        // Handle any disconnected Tectons (not reachable via neighbors)
        for (Tecton tecton : tectons) {
            if (!visited.contains(tecton)) {
                // Fallback to original grid-based positioning
                double[] pos = getFallbackPosition(rows, cols, hexWidth, hexHeight);
                assignPosition(tecton, pos[0], pos[1], hexWidth, hexHeight, tectonVMs);
                visited.add(tecton);
            }
        }

        for (Tecton tecton : assignedPositions.keySet()) {
            double[] position = assignedPositions.get(tecton);
            TectonViewModel vm = new TectonViewModel(tecton, position[0] + hexWidth / 2 + 10, position[1] + hexHeight / 2 + 10);
            tectonVMs.put(tecton, vm);
            addEntity(vm);
        }

        return tectonVMs;
    }

    private void assignPosition(Tecton tecton, double x, double y, double hexWidth, double hexHeight,
            Map<Tecton, TectonViewModel> tectonVMs) {
        assignedPositions.put(tecton, new double[] { x, y });

        // Mark the approximate grid position as occupied
        int row = (int) (y / (hexHeight + 10.0));
        int col = (int) (x / (hexWidth + 10.0));
        occupiedCoords.add(row + "," + col);
    }

    private double[] findNeighborPosition(int currentRow, int currentCol, int rows, int cols, double hexWidth,
            double hexHeight) {
        double spacing = 10.0;

        // Try each possible hexagonal neighbor position
        for (double[] offset : HEX_OFFSETS) {
            int newRow = currentRow + (int) offset[1];
            int newCol = currentCol + (int) offset[0];

            // Check if the position is within bounds and not occupied
            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols
                    && !occupiedCoords.contains(newRow + "," + newCol)) {
                // Calculate x and y with hexagonal offset
                double offsetX = (newRow % 2 == 0) ? 0 : (hexWidth + spacing) / 2;
                double x = newCol * (hexWidth + spacing) + offsetX;
                double y = newRow * (hexHeight + spacing);
                return new double[] { x, y };
            }
        }
        return null; // No valid position found
    }

    private double[] getFallbackPosition(int rows, int cols, double hexWidth, double hexHeight) {
        int row = occupiedPosition / cols;
        int col = occupiedPosition % cols;
        double spacing = 10.0;
        double offsetX = (row % 2 == 0) ? 0 : (hexWidth + spacing) / 2;
        double x = col * (hexWidth + spacing) + offsetX;
        double y = row * (hexHeight + spacing);
        occupiedPosition++;
        occupiedCoords.add(row + "," + col);
        return new double[] { x, y };
    }

    // Placeholder for the addEntity method (as per your original code)
    private void addEntity(TectonViewModel vm) {
        entityController.addEntity(vm);
    }
}