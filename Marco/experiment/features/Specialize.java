package features;

import java.util.HashSet;
import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Nothing;
import org.dllearner.refinementoperators.RefinementOperator;

public class Specialize {

	public static Set<Description> specialize(Description concept,
			RefinementOperator r, int maxLength, int depth) {

		Set<Description> childs = new HashSet<Description>();
//
		if (!(concept.toString().contains(" OR ") || concept.toString().contains(" AND ") || concept.toString().startsWith("(NOT ") || concept.toString().startsWith("BOTTOM")))
		{
			childs = r.refine(concept, maxLength);
		}
		System.out.println("p: " + depth + " - childs.size: " + childs.size());

		Set<Description> appendChilds = new HashSet<Description>();
		for (Description child : childs) {

			// System.out.println("													contenuto");
			appendChilds.addAll(specialize(child, r, maxLength, depth + 1));
			
			if (child.toString().startsWith("EXISTS ") || child.getLength() == 1)
				appendChilds.add(child);
		}
		return appendChilds;
	}

}
