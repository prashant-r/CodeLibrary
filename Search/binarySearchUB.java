/*
	 * Binary seach through the keys to get either the exact match or the upper bound
	 * capacity that fits this request.
	 */
	public static Integer binarySearchUB(List<Integer> searchSet, Integer toFind)
	{
		int low = 0;
		int high = searchSet.size()-1;
		int mid = low;
		while(low < high)
		{
			mid = low + (high - low )/2;
			int res = searchSet.get(mid).compareTo(toFind);
			if(res >0) high = mid;
			else low = mid+1;
		}
		if (searchSet.get(mid).compareTo(toFind) == 0) high--;
		if (searchSet.get(high).compareTo(toFind) < 0) return null;
		return searchSet.get(high);
	}
