package whereapp.kps.cpe.com.wheresapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by apple on 4/30/15 AD.
 */
public class shortedpath {
    private Stack<Integer> stack;
    public shortedpath()
    {
        stack = new Stack<>();
    }
    private List<Integer> path = new ArrayList<>();

    public List<Integer> tsp(int adjacencyMatrix[][])
    {

        int numberOfNodes = adjacencyMatrix[1].length - 1;
        int[] visited = new int[numberOfNodes + 1];
        visited[1] = 1;
        stack.push(1);
        int element, dst = 0, i;
        boolean minFlag = false;
        System.out.print(1);
        while (!stack.isEmpty())
        {
            element = stack.peek();
            i = 1;
            int min = Integer.MAX_VALUE;
            while (i <= numberOfNodes)
            {
                if (adjacencyMatrix[element][i] > 1 && visited[i] == 0)
                {
                    if (min > adjacencyMatrix[element][i])
                    {
                        min = adjacencyMatrix[element][i];
                        dst = i;
                        minFlag = true;
                    }
                }
                i++;
            }
            if (minFlag)
            {
                visited[dst] = 1;
                stack.push(dst);
                path.add(dst);
                minFlag = false;
                continue;
            }
            stack.pop();
        }
        return path;
    }
}