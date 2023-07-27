package derg;


import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


public class LembdaTraitKeyShuffle implements CanLembdaUpdate{

	private List<StringLembda> tree;
	private int treeSize, 
	depthChunk;//used as how many 'leaf' nodes exist. However, they do not have to be actual leaves
	//we just care about the bottom ones on the tree
	
	public LembdaTraitKeyShuffle(StringLembda...lembdas) {
		tree = Arrays.asList(lembdas);
		treeSize = tree.size();
		depthChunk = -1;
	}
	
	/**
	 * i = the number of layers you want to look up
	 * @param i
	 * @return
	 */
	public List<StringLembda> leafLayer(char forword, int i){
		return null;
	}
	
	private int parent(int n) {
		return (n-1)/2;
	}
	
	private void trickle(int n) {
		int cur = parent(n);
		int was = n;
		StringLembda sl;
		while (cur != 0) {
			sl = tree.get(cur);
			tree.set(cur,tree.get(was));
			tree.set(was,sl);
			sl.users.put(this,was);//now the lemba knows where it is to further update
			was = cur;
			cur = parent(cur);
		}
	}

	public String next(char forword) {
		return null;//FIXME: search the tree from bottom to top, trying to get 3 choices to choose from in each layer
		//if have to go to next layer, just choose from the ones you have
		//note that this will probably perform poorly if the tree is too small
		//maybe just get first 3
		//after you find something, call the update function on it
		//this will force yourself and other trees using it to put it to the top of the tree, and trickle down
	}

	@Override
	public void update(StringLembda sl) {
		int spot = sl.users.get(this);
		trickle(spot);
	}
	
}
