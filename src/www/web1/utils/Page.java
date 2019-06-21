package www.web1.utils;
/**用于分页的类
 * 
 */
public class Page {

	int start=0;
	int count = 5;
	int last = 0;
	int type = 0;
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		if(start >= 0){
			this.start = start;
		}else{
			this.start = 0;
		}
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getLast() {
		return last;
	}
	public void setLast(int last) {
		this.last = last;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	/**计算最后一页的起始位置
	 * @param 待分页内容的size
	 */
	public void caculateLast(int total) {
	    // 假设总数是50，是能够被5整除的，那么最后一页的开始就是45
	    if (0 == total % count)
	        last = total - count;
	    // 假设总数是51，不能够被5整除的，那么最后一页的开始就是50
	    else
	        last = total - total % count;		
	}

}
