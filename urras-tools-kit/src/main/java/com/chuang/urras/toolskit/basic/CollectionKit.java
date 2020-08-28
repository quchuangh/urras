package com.chuang.urras.toolskit.basic;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 集合相关工具类，包括数组
 * 
 * @author xiaoleilu
 * 
 */
public class CollectionKit {
	private static final Random random = new Random();
	private CollectionKit() {
		// 静态类不可实例化
	}

	/**
	 * 以 conjunction 为分隔符将集合转换为字符串
	 * 
	 * @param <T> 被处理的集合
	 * @param collection 集合
	 * @param conjunction 分隔符
	 * @return 连接后的字符串
	 */
	public static <T> String join(Iterable<T> collection, String conjunction) {
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (T item : collection) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(conjunction);
			}
			sb.append(item);
		}
		return sb.toString();
	}

	public static <T> List<T> nullToEmpty(@Nullable List<T> list) {
		return null == list ? Collections.EMPTY_LIST : list;
	}

	public static <T> Set<T> nullToEmpty(@Nullable Set<T> list) {
		return null == list ? Collections.EMPTY_SET : list;
	}

	public static <T> void ifNotNull(@Nullable Collection<T> list, Consumer<Collection<T>> ifNotNull) {
		if(null != list) {
			ifNotNull.accept(list);
		}

	}

	public static <K, V> Map<K, V> nullToEmpty(@Nullable Map<K, V> map) {
		return null == map ? Collections.EMPTY_MAP : map;
	}

	/**
	 * 以 conjunction 为分隔符将数组转换为字符串
	 * 
	 * @param <T> 被处理的集合
	 * @param array 数组
	 * @param conjunction 分隔符
	 * @return 连接后的字符串
	 */
	public static <T> String join(T[] array, String conjunction) {
		return join(Arrays.asList(array), conjunction);
	}

	public static <CT extends Collection<T>, CR extends Collection<R>, T, R> CR map(CT ct, Supplier<CR> crGetter, Function<T, R> map) {
		CR cr = crGetter.get();
		for(T t : ct) {
			R r = map.apply(t);
			cr.add(r);
		}

		return cr;
	}


    @SuppressWarnings("unchecked")
	public static <T> T randomOne(Collection<T> coll) {
		int index = random.nextInt(coll.size());
		return (T) coll.toArray()[index];
	}

	public static <T> T randomOne(Collection<T> coll, Function<T, Integer> weight) {
		int total = coll.stream().map(weight).reduce(Integer::sum).get();
		int current = random.nextInt(total);
		for(T obj: coll) {
			int w = weight.apply(obj);
			if(current < w) {
				return obj;
			} else {
				current -= w;
			}
		}

		throw new RuntimeException("方法计算错误");
	}

	public static <T> T randomOne(T... tn) {
		int current = random.nextInt(tn.length);
		return tn[current];
	}

	/**
	 * 将多个集合排序并显示不同的段落（分页）
	 * @param pageNo 页码
	 * @param numPerPage 每页的条目数
	 * @param comparator 比较器
	 * @param colls 集合数组
	 * @return 分页后的段落内容
	 */
	@SafeVarargs
	public static <T> List<T> sortPageAll(int pageNo, int numPerPage, Comparator<T> comparator, Collection<T>... colls) {
		final List<T> result = new ArrayList<>();
		for (Collection<T> coll : colls) {
			result.addAll(coll);
		}
		
		result.sort(comparator);
		
		//第一页且数目少于第一页显示的数目
		if(pageNo <=1 && result.size() <= numPerPage) {
			return result;
		}
		
		final int[] startEnd = PageKit.transToStartEnd(pageNo, numPerPage);
		return result.subList(startEnd[0], startEnd[1]);
	}

	/**
	 * 将Set排序（根据Entry的值）
	 *
	 * @param set 被排序的Set
	 * @return 排序后的Set
	 */
	public static List<Entry<Long, Long>> sortEntrySetToList(Set<Entry<Long, Long>> set) {
		List<Entry<Long, Long>> list = new LinkedList<>(set);
		list.sort((o1, o2) -> {
            if (o1.getValue() > o2.getValue()) {
                return 1;
            }
            if (o1.getValue() < o2.getValue()) {
                return -1;
            }
            return 0;
        });
		return list;
	}

	/**
	 * 切取部分数据
	 *
	 * @param <T> 集合元素类型
	 * @param surplusAlaDatas 原数据
	 * @param partSize 每部分数据的长度
	 */
	public static <T> List<T> popPart(Stack<T> surplusAlaDatas, int partSize) {
		if (surplusAlaDatas.size() <= 0){
			return Collections.emptyList();
		}

		final List<T> currentAlaDatas = new ArrayList<>();
		int size = surplusAlaDatas.size();
		// 切割
		if (size > partSize) {
			for (int i = 0; i < partSize; i++) {
				currentAlaDatas.add(surplusAlaDatas.pop());
			}
		} else {
			for (int i = 0; i < size; i++) {
				currentAlaDatas.add(surplusAlaDatas.pop());
			}
		}
		return currentAlaDatas;
	}

	/**
	 * 切取部分数据
	 *
	 * @param <T> 集合元素类型
	 * @param surplusAlaDatas 原数据
	 * @param partSize 每部分数据的长度
	 * @return 切取出的数据或null
	 */
	public static <T> List<T> popPart(Deque<T> surplusAlaDatas, int partSize) {
		if (surplusAlaDatas.size() <= 0){
            return Collections.emptyList();
		}

		final List<T> currentAlaDatas = new ArrayList<>();
		int size = surplusAlaDatas.size();
		// 切割
		if (size > partSize) {
			for (int i = 0; i < partSize; i++) {
				currentAlaDatas.add(surplusAlaDatas.pop());
			}
		} else {
			for (int i = 0; i < size; i++) {
				currentAlaDatas.add(surplusAlaDatas.pop());
			}
		}
		return currentAlaDatas;
	}




	/**
	 * 将新元素添加到已有数组中<br/>
	 * 添加新元素会生成一个新的数组，不影响原数组
	 *
	 * @param buffer 已有数组
	 * @param newElement 新元素
	 * @return 新数组
	 */
	public static <T, C extends T> T[] append(T[] buffer, C newElement) {
		T[] t = resize(buffer, buffer.length + 1, buffer.getClass().getComponentType());
		t[buffer.length] = newElement;
		return t;
	}

	/**
	 * 生成一个新的重新设置大小的数组
	 *
	 * @param buffer 原数组
	 * @param newSize 新的数组大小
	 * @param componentType 数组元素类型
	 * @return 调整后的新数组
	 */
	public static <T> T[] resize(T[] buffer, int newSize, Class<?> componentType) {
		T[] newArray = newArray(componentType, newSize);
		System.arraycopy(buffer, 0, newArray, 0, buffer.length >= newSize ? newSize : buffer.length);
		return newArray;
	}

	/**
	 * 新建一个空数组
	 * @param componentType 元素类型
	 * @param newSize 大小
	 * @return 空数组
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] newArray(Class<?> componentType, int newSize) {
		return (T[]) Array.newInstance(componentType, newSize);
	}

	/**
	 * 生成一个新的重新设置大小的数组<br/>
	 * 新数组的类型为原数组的类型
	 *
	 * @param buffer 原数组
	 * @param newSize 新的数组大小
	 * @return 调整后的新数组
	 */
	public static <T> T[] resize(T[] buffer, int newSize) {
		return resize(buffer, newSize, buffer.getClass().getComponentType());
	}

	/**
	 * 将多个数组合并在一起<br>
	 * 忽略null的数组
	 *
	 * @param arrays 数组集合
	 * @return 合并后的数组
	 */
	@SafeVarargs
	public static <T> T[] addAll(T[]... arrays) {
		if (arrays.length == 1) {
			return arrays[0];
		}

		int length = 0;
		for (T[] array : arrays) {
			if(array == null) {
				continue;
			}
			length += array.length;
		}
		T[] result = newArray(arrays.getClass().getComponentType().getComponentType(), length);

		length = 0;
		for (T[] array : arrays) {
			if(array == null) {
				continue;
			}
			System.arraycopy(array, 0, result, length, array.length);
			length += array.length;
		}
		return result;
	}

	/**
	 * 生成一个数字列表<br>
	 * 自动判定正序反序
	 * @param excludedEnd 结束的数字（不包含）
	 * @return 数字列表
	 */
	public static int[] range(int excludedEnd) {
		return range(0, excludedEnd, 1);
	}

	/**
	 * 生成一个数字列表<br>
	 * 自动判定正序反序
	 * @param includedStart 开始的数字（包含）
	 * @param excludedEnd 结束的数字（不包含）
	 * @return 数字列表
	 */
	public static int[] range(int includedStart, int excludedEnd) {
		return range(includedStart, excludedEnd, 1);
	}

	/**
	 * 生成一个数字列表<br>
	 * 自动判定正序反序
	 * @param includedStart 开始的数字（包含）
	 * @param excludedEnd 结束的数字（不包含）
	 * @param step 步进
	 * @return 数字列表
	 */
	public static int[] range(int includedStart, int excludedEnd, int step) {
		if(includedStart > excludedEnd) {
			int tmp = includedStart;
			includedStart = excludedEnd;
			excludedEnd = tmp;
		}

		if(step <=0) {
			step = 1;
		}

		int deviation = excludedEnd - includedStart;
		int length = deviation / step;
		if(deviation % step != 0) {
			length += 1;
		}
		int[] range = new int[length];
		for(int i = 0; i < length; i++) {
			range[i] = includedStart;
			includedStart += step;
		}
		return range;
	}

	/**
	 * 截取数组的部分
	 * @param list 被截取的数组
	 * @param start 开始位置（包含）
	 * @param end 结束位置（不包含）
	 * @return 截取后的数组，当开始位置超过最大时，返回null
	 */
	public static <T> List<T> sub(List<T> list, int start, int end) {
		if(list == null || list.isEmpty()) {
			return null;
		}

		if(start < 0) {
			start = 0;
		}
		if(end < 0) {
			end = 0;
		}

		if(start > end) {
			int tmp = start;
			start = end;
			end = tmp;
		}

		final int size = list.size();
		if(end > size) {
			if(start >= size) {
				return null;
			}
			end = size;
		}

		return list.subList(start, end);
	}

	/**
	 * 截取集合的部分
	 * @param list 被截取的数组
	 * @param start 开始位置（包含）
	 * @param end 结束位置（不包含）
	 * @return 截取后的数组，当开始位置超过最大时，返回null
	 */
	public static <T> List<T> sub(Collection<T> list, int start, int end) {
		if(list == null || list.isEmpty()) {
			return null;
		}

		return sub(new ArrayList<>(list), start, end);
	}

	/**
	 * 数组是否为空
	 * @param array 数组
	 * @return 是否为空
	 */
	public static <T> boolean isEmpty(@Nullable T[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 数组是否为非空
	 * @param array 数组
	 * @return 是否为非空
	 */
	public static <T> boolean isNotEmpty(@Nullable T[] array) {
		return !isEmpty(array);
	}

	/**
	 * 集合是否为空
	 * @param collection 集合
	 * @return 是否为空
	 */
	public static boolean isEmpty(@Nullable Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 * 集合是否为非空
	 * @param collection 集合
	 * @return 是否为非空
	 */
	public static boolean isNotEmpty(@Nullable Collection<?> collection) {
		return !isEmpty(collection);
	}

	/**
	 * Map是否为空
	 * @param map 集合
	 * @return 是否为空
	 */
	public static boolean isEmpty(@Nullable Map<?, ?> map) {
		return null == map || map.isEmpty();
	}

	/**
	 * Map是否为非空
	 * @param map 集合
	 * @return 是否为非空
	 */
	public static <T> boolean isNotEmpty(@Nullable Map<?, ?> map) {
		return !isEmpty(map);
	}

	/**
	 * 映射键值（参考Python的zip()函数）<br>
	 * 例如：<br>
	 * 		keys =    [a,b,c,d]<br>
	 *		values = [1,2,3,4]<br>
	 * 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
	 * 如果两个数组长度不同，则只对应最短部分
	 * @param keys 键列表
	 * @param values 值列表
	 * @return Map
	 */
	public static <T, K> Map<T, K> zip(T[] keys, K[] values) {
		if(isEmpty(keys) || isEmpty(values)) {
			return null;
		}

		final int size = Math.min(keys.length, values.length);
		final Map<T, K> map = new HashMap<>((int)(size / 0.75));
		for(int i = 0; i < size; i++) {
			map.put(keys[i], values[i]);
		}

		return map;
	}

	/**
	 * 映射键值（参考Python的zip()函数）<br>
	 * 例如：<br>
	 * 		keys =    a,b,c,d<br>
	 *		values = 1,2,3,4<br>
	 *		delimiter = ,
	 * 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
	 * 如果两个数组长度不同，则只对应最短部分
	 * @param keys 键列表
	 * @param values 值列表
	 * @return Map
	 */
	public static Map<String, String> zip(String keys, String values, String delimiter) {
		return zip(StringKit.split(keys, delimiter), StringKit.split(values, delimiter));
	}
	
	/**
	 * 映射键值（参考Python的zip()函数）<br>
	 * 例如：<br>
	 * 		keys =    [a,b,c,d]<br>
	 *		values = [1,2,3,4]<br>
	 * 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
	 * 如果两个数组长度不同，则只对应最短部分
	 * @param keys 键列表
	 * @param values 值列表
	 * @return Map
	 */
	public static <T, K> Map<T, K> zip(Collection<T> keys, Collection<K> values) {
		if(isEmpty(keys) || isEmpty(values)) {
			return null;
		}
		
		final List<T> keyList = new ArrayList<>(keys);
		final List<K> valueList = new ArrayList<>(values);
		
		final int size = Math.min(keys.size(), values.size());
		final Map<T, K> map = new HashMap<>((int)(size / 0.75));
		for(int i = 0; i < size; i++) {
			map.put(keyList.get(i), valueList.get(i));
		}
		
		return map;
	}
	
	/**
	 * 数组中是否包含元素
	 * @param array 数组
	 * @param value 被检查的元素
	 * @return 是否包含
	 */
	public static <T> boolean contains(T[] array, T value) {
		final Class<?> componetType = array.getClass().getComponentType();
		boolean isPrimitive = false;
		if(null != componetType) {
			isPrimitive = componetType.isPrimitive();
		}
		for (T t : array) {
			if(t == value) {
				return true;
			}else if(!isPrimitive && null != value && value.equals(t)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 将Entry集合转换为HashMap
	 * @param entryCollection entry集合
	 * @return Map
	 */
	public static <T, K> HashMap<T, K> toMap(Collection<Entry<T, K>> entryCollection) {
		HashMap<T,K> map = new HashMap<>();
		for (Entry<T, K> entry : entryCollection) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}
	
	/**
	 * 将集合转换为排序后的TreeSet
	 * @param collection 集合
	 * @param comparator 比较器
	 * @return treeSet
	 */
	public static <T> TreeSet<T> toTreeSet(Collection<T> collection, Comparator<T> comparator){
		final TreeSet<T> treeSet = new TreeSet<>(comparator);
        treeSet.addAll(collection);
		return treeSet;
	}
	
	/**
	 * 排序集合
	 * @param collection 集合
	 * @param comparator 比较器
	 * @return treeSet
	 */
	public static <T> List<T> sort(Collection<T> collection, Comparator<T> comparator){
	    List<T> list = new ArrayList<>(collection);
		list.sort(comparator);
		return list;
	}

	public static <K, V> Map<K,V> sortMap(Map<K, V> map, Comparator<K> keyComp) {
		return map.keySet().stream().sorted(keyComp)
                .collect(LinkedHashMap::new,
                        (Map<K,V> m, K k) -> m.put(k, map.get(k)),
                        (m1, m2) -> {});
    }

	//------------------------------------------------------------------- 基本类型的数组转换为包装类型数组
	/**
	 * 将基本类型数组包装为包装类型
	 * @param values 基本类型数组
	 * @return 包装类型数组
	 */
	public static Integer[] wrap(int... values){
		final int length = values.length;
		Integer[] array = new Integer[length];
		for(int i = 0; i < length; i++){
			array[i] = values[i];
		}
		return array;
	}
	
	/**
	 * 将基本类型数组包装为包装类型
	 * @param values 基本类型数组
	 * @return 包装类型数组
	 */
	public static Long[] wrap(long... values){
		final int length = values.length;
		Long[] array = new Long[length];
		for(int i = 0; i < length; i++){
			array[i] = values[i];
		}
		return array;
	}
	
	/**
	 * 将基本类型数组包装为包装类型
	 * @param values 基本类型数组
	 * @return 包装类型数组
	 */
	public static Character[] wrap(char... values){
		final int length = values.length;
		Character[] array = new Character[length];
		for(int i = 0; i < length; i++){
			array[i] = values[i];
		}
		return array;
	}
	
	/**
	 * 将基本类型数组包装为包装类型
	 * @param values 基本类型数组
	 * @return 包装类型数组
	 */
	public static Byte[] wrap(byte... values){
		final int length = values.length;
		Byte[] array = new Byte[length];
		for(int i = 0; i < length; i++){
			array[i] = values[i];
		}
		return array;
	}
	
	/**
	 * 将基本类型数组包装为包装类型
	 * @param values 基本类型数组
	 * @return 包装类型数组
	 */
	public static Short[] wrap(short... values){
		final int length = values.length;
		Short[] array = new Short[length];
		for(int i = 0; i < length; i++){
			array[i] = values[i];
		}
		return array;
	}
	
	/**
	 * 将基本类型数组包装为包装类型
	 * @param values 基本类型数组
	 * @return 包装类型数组
	 */
	public static Float[] wrap(float... values){
		final int length = values.length;
		Float[] array = new Float[length];
		for(int i = 0; i < length; i++){
			array[i] = values[i];
		}
		return array;
	}
	
	/**
	 * 将基本类型数组包装为包装类型
	 * @param values 基本类型数组
	 * @return 包装类型数组
	 */
	public static Double[] wrap(double... values){
		final int length = values.length;
		Double[] array = new Double[length];
		for(int i = 0; i < length; i++){
			array[i] = values[i];
		}
		return array;
	}
	
	/**
	 * 将基本类型数组包装为包装类型
	 * @param values 基本类型数组
	 * @return 包装类型数组
	 */
	public static Boolean[] wrap(boolean... values){
		final int length = values.length;
		Boolean[] array = new Boolean[length];
		for(int i = 0; i < length; i++){
			array[i] = values[i];
		}
		return array;
	}
	
	/**
	 * 判定给定对象是否为数组类型
	 * @param obj 对象
	 * @return 是否为数组类型
	 */
	public static boolean isArray(Object obj){
		return obj.getClass().isArray();
	}
	
	/**
	 * 数组或集合转String
	 * 
	 * @param obj 集合或数组对象
	 * @return 数组字符串，与集合转字符串格式相同
	 */
	public static String toString(Object obj) {
		if (isArray(obj)) {
			try {
				return Arrays.deepToString((Object[]) obj);
			} catch (Exception e) {
				final String className = obj.getClass().getComponentType().getName();
				switch (className) {
					case "long":
						return Arrays.toString((Long[]) obj);
					case "int":
						return Arrays.toString((Integer[]) obj);
					case "short":
						return Arrays.toString((Short[]) obj);
					case "char":
						return Arrays.toString((Character[]) obj);
					case "byte":
						return Arrays.toString((Byte[]) obj);
					case "boolean":
						return Arrays.toString((Boolean[]) obj);
					case "float":
						return Arrays.toString((Float[]) obj);
					case "double":
						return Arrays.toString((Double[]) obj);
					default:
						throw e;
				}
			}
		}
		return obj.toString();
	}

	/**
	 * 取第一个集合对第二个集合的 差集。
     * 第一个集合中所有 不在第二个集合 的元素
	 * @param one 第一个集合
	 * @param two 第二个集合
	 * @param getter 新集合获取方法
	 * @param <T>
	 * @param <C>
	 * @return
	 */
	public static <T, C extends Collection<T>> C subtract(Collection<T> one, Collection<T> two, Supplier<C> getter) {
		C newC = getter.get();
		for(T t : one) {
			if(!two.contains(t)) {
				newC.add(t);
			}
		}

		return newC;
	}

	public static <T, C extends Collection<T>> C union(Collection<T> one, Collection<T> two, Supplier<C> getter) {
		Set<T> set = new HashSet<>();
		set.addAll(one);
		set.addAll(two);
		C newC = getter.get();
		newC.addAll(set);
		return newC;
	}

}
