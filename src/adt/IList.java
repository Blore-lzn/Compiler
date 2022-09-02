package adt;

import java.util.Iterator;

public class IList<N, L> implements Iterable<INode<N, L>> {
    private INode<N, L> begin = null;
    private INode<N, L> end = null;
    private final L val;
    private int size = 0;
    
    public IList(L val) {
        this.val = val;
    }
    
    public INode<N, L> getBegin() {
        return begin;
    }
    
    public void setBegin(INode<N, L> begin) {
        this.begin = begin;
    }
    
    public INode<N, L> getEnd() {
        return end;
    }
    
    public void setEnd(INode<N, L> end) {
        this.end = end;
    }
    
    public L getVal() {
        return val;
    }
    
    public int getSize() {
        return size;
    }
    
    // 只改变size
    public void addNode() {
        size++;
    }
    
    // 只改变size
    public void removeNode() {
        size--;
    }
    
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    // 正向遍历器
    public Iterator<INode<N, L>> iterator() {
        return new ListIterator(begin);
    }
    
    // TODO: 反向遍历器
    
    class ListIterator implements Iterator<INode<N, L>> {
        INode<N, L> now = new INode<>(null);
        INode<N, L> next = null;
        
        public ListIterator(INode<N, L> head) {
            now.setNext(head);
        }
        
        @Override
        public boolean hasNext() {
            return next != null || now.getNext() != null;
        }
        
        @Override
        public INode<N, L> next() {
            if (next == null) {
                now.setNext(now.getNext());
            } else {
                now = next;
            }
            next = null;
            return now;
        }
        
        @Override
        public void remove() {
            INode<N, L> prev = now.getPrev();
            INode<N, L> next = now.getNext();
            IList<N, L> parent = now.getParent();
            
            if (prev != null) {
                prev.setNext(next);
            } else {
                parent.setBegin(next);
            }
            
            if (next != null) {
                next.setPrev(prev);
            } else {
                parent.setEnd(prev);
            }
            
            parent.removeNode();
            
            this.next = next;
            
            now.clear();
        }
    }
    
    
}
