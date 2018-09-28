import java.util.Comparator;


// a comparator used by the priority queue in the path finding algorithm (see EdgePathFinder::findPath())
public class NodePriorityComparator implements Comparator<PathFindingNode>{

	@Override
	public int compare(PathFindingNode arg0, PathFindingNode arg1) 
	{
        if (arg0.getPriority() < arg1.getPriority())
            return -1;
        else if (arg0.getPriority() > arg1.getPriority())
            return 1;
		return 0;
	}
}
