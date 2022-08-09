package insa.lyon.justif.utils.algo.tsp;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * IteratorSeq : This class is an iterator to iterate over the different unvisited nodes during the
 * Branch and Bound algorithm.
 */
public class IteratorSeq implements Iterator<String> {

    private String[] candidats;
    private int nbCandidats;

    /**
     * Constructor : Create an iterator to iterate over the different unvisited nodes which are successors of the
     * current node by order of apparition in the "unvisited" collection given in input.
     *
     * @param unvisited The collection of unvisited nodes.
     */
    public IteratorSeq(Collection<String> unvisited) {
        this.candidats = new String[unvisited.size()];
        Iterator<String> it = unvisited.iterator();
        while (it.hasNext()) {
            String s = it.next();
            candidats[nbCandidats++] = s;
        }
    }

    @Override
    public boolean hasNext() {
        return nbCandidats > 0;
    }

    @Override
    public String next() {
        if(!hasNext()){
            throw new NoSuchElementException();
        }
        nbCandidats--;
        return candidats[nbCandidats];
    }

}

