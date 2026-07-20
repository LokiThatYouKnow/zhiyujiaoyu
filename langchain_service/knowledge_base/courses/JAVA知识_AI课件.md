# Java 基础知识教学课件

---

## 一、课程概述

### 1. 课程简介
Java 是一种广泛使用的计算机编程语言，拥有跨平台、面向对象、安全性高等特性。本课程从零基础出发，系统讲解 Java 的核心语法、面向对象思想、常用类库及基础开发环境搭建，帮助学习者建立扎实的 Java 编程功底，为后续 Java Web、Android 开发及企业级应用学习打下坚实基础。

### 2. 教学目标
- 理解 Java 语言的发展历史、技术特点及运行机制
- 掌握 Java 基本语法：变量、数据类型、运算符、流程控制
- 深刻理解面向对象三大特性：封装、继承、多态
- 熟练使用 Java 常用核心类库（String、集合、包装类等）
- 能够独立编写结构清晰、逻辑正确的 Java 控制台应用程序

### 3. 适用对象
- 高职/本科计算机相关专业学生
- 编程零基础的 Java 初学者
- 具备一定计算机操作基础，希望系统学习 Java 的编程爱好者

### 4. 参考学时
- 理论讲授：24 学时
- 上机实践：24 学时
- 合计：48 学时

---

## 二、知识体系

- 第1章 走进 Java
  - Java 发展史与版本演进
  - Java 语言特点
  - JVM、JRE、JDK 的概念与关系
  - 第一个 Java 程序：HelloWorld
  - 开发环境搭建（JDK 安装、IDE 使用）
- 第2章 Java 基本语法
  - 标识符与关键字
  - 变量与数据类型（基本类型与引用类型）
  - 运算符与表达式
  - 流程控制语句（分支、循环）
  - 数组的定义与使用
- 第3章 面向对象编程（核心）
  - 类与对象
  - 封装与访问控制
  - 构造方法
  - this 关键字
  - static 关键字
  - 继承
  - 方法重写
  - super 关键字
  - 多态
  - 抽象类与接口
  - 内部类
- 第4章 Java 常用核心类
  - String 与 StringBuilder
  - 包装类
  - Math 类
  - 日期时间类（LocalDate、LocalDateTime 等）
  - 集合框架：List、Set、Map
  - 泛型
- 第5章 异常处理与 I/O 基础（扩展）
  - try-catch-finally
  - 自定义异常
  - File 类
  - 字节流与字符流

---

## 三、详细教学内容

### 第1章 走进 Java

#### 1.1 Java 发展史与语言特点
- **诞生**：1995 年由 Sun Microsystems 公司发布，创始人 James Gosling。
- **名称由来**：源于印尼爪哇岛，一种咖啡的名称。
- **版本演进**：
  - Java SE（标准版）：桌面应用、基础核心。
  - Java EE（企业版）：Web 应用、分布式系统。
  - Java ME（微型版）：嵌入式移动设备（逐渐被 Android 取代）。
- **核心特点**：
  - **跨平台性**：借助 Java 虚拟机（JVM），实现“一次编译，到处运行”。
  - **面向对象**：纯粹的对象模型，支持封装、继承、多态。
  - **健壮性**：强类型语言，提供异常处理与垃圾回收机制。
  - **安全性**：取消了指针，提供类加载机制与字节码校验。

#### 1.2 JVM、JRE、JDK
- **JVM**（Java Virtual Machine）：Java 虚拟机，运行字节码文件（.class）的 “计算机”。
- **JRE**（Java Runtime Environment）：Java 运行环境 = JVM + 核心类库，用于运行 Java 程序。
- **JDK**（Java Development Kit）：Java 开发工具包 = JRE + 开发工具（编译器 javac、打包工具 jar 等）。
- **关系**：开发时安装 JDK（里面包含 JRE），运行程序只需 JRE。

**重点**：JDK 的安装和 bin 目录的 Path 环境变量配置是后续开发的基础。

#### 1.3 第一个程序：HelloWorld
```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, Java!");
    }
}
```
- 编译：`javac HelloWorld.java` → 生成 `HelloWorld.class` 字节码文件。
- 运行：`java HelloWorld` → JVM 执行字节码，输出结果。
- **代码解析**：
  - `public class` 声明公共类，类名必须与文件名一致。
  - `main` 方法是程序入口，格式固定。
  - `System.out.println` 输出并换行。

**难点**：类名大小写敏感，命令行运行时不含 .class 扩展名。

---

### 第2章 Java 基本语法

#### 2.1 标识符与关键字
- **标识符**：类名、变量名、方法名等自定义名称。
  - 规则：字母、数字、下划线、$ 组成，不能以数字开头，不能使用关键字。
  - 习惯：类名首字母大写（Pascal），变量与方法小写（camelCase）。
- **关键字**：Java 预定义的有特殊含义的单词，如 `int`、`class`、`public`、`static`、`void` 等。

#### 2.2 变量与数据类型
- **变量**：内存中一块存储区域，必须先声明再使用。
- **数据类型分类**：

| 类型 | 关键字 | 大小/范围 | 示例 |
|------|--------|-----------|------|
| 字节型 | byte | 1字节 (-128~127) | `byte b = 100;` |
| 短整型 | short | 2字节 | `short s = 1000;` |
| 整型 | int | 4字节 (约±21亿) | `int i = 10;` |
| 长整型 | long | 8字节 | `long l = 100L;` |
| 单精度 | float | 4字节 | `float f = 3.14f;` |
| 双精度 | double | 8字节 | `double d = 3.14;` |
| 字符型 | char | 2字节 (Unicode) | `char c = 'A';` |
| 布尔型 | boolean | 1字节 | `boolean flag = true;` |

- **引用类型**：类、接口、数组等，变量存储的是对象的引用（地址）。

**重点**：
- 整数默认 int，小数默认 double → 赋值给 long 或 float 需加后缀 L/f。
- 自动类型提升（小范围转大范围）与强制转换（可能损失精度）。

#### 2.3 运算符与表达式
- **算术**：+ - * / % （取余）、++ --
- **关系**：> < >= <= == !=
- **逻辑**：&&（短路与）、||（短路或）、!（非）
- **赋值**：= += -= *= /= %=
- **三元运算符**：`条件 ? 值1 : 值2`

**难点**：++ 前后区别、短路逻辑的判断效率。

#### 2.4 流程控制
- **分支结构**：
  - if、if-else、if-else if、switch（byte/short/int/char/String/枚举）
- **循环结构**：
  - for、while、do-while
  - 增强 for 循环（用于遍历数组与集合）
- **控制语句**：break、continue

**示例**：计算 1~100 的累加和
```java
int sum = 0;
for (int i = 1; i <= 100; i++) {
    sum += i;
}
System.out.println("和：" + sum);
```

#### 2.5 数组
- **一维数组**：
  - 声明：`int[] arr;` 或 `int arr[];`
  - 动态初始化：`arr = new int[5];`（默认值）
  - 静态初始化：`int[] arr = {1,2,3,4,5};`
- **数组属性**：`arr.length` 获取元素个数。
- **遍历**：for 循环或增强 for `for(int e : arr)`。
- **二维数组**：`int[][] matrix = new int[3][4];` 视为数组的数组。

**重点**：数组存储相同类型元素，长度固定。常见错误：数组下标越界（ArrayIndexOutOfBoundsException）。

---

### 第3章 面向对象编程

#### 3.1 类与对象
- **类**：对现实事物抽象描述的模板，包含属性（成员变量）和行为（方法）。
- **对象**：类的具体实例。通过 `new 类名()` 创建。
- **类的定义**：
```java
public class Student {
    // 属性
    String name;
    int age;
    // 方法
    public void study() {
        System.out.println(name + "在学习");
    }
}
```
- **对象使用**：`Student stu = new Student(); stu.name = "张三"; stu.study();`

#### 3.2 封装与访问控制
- **封装**：将数据和操作数据的方法捆绑，隐藏内部细节。
- **权限修饰符**：
  - `private`：本类可见。
  - 默认（缺省）：同包可见。
  - `protected`：同包、子类可见。
  - `public`：所有类可见。
- **标准封装**：属性私有化（private），提供公共的 get/set 方法进行安全访问。
```java
private int age;
public void setAge(int age) {
    if (age >= 0) this.age = age;
}
public int getAge() { return age; }
```

#### 3.3 构造方法
- 方法名与类名相同，无返回值（也不能写 void）。
- 作用：完成对象的初始化，通常为成员变量赋值。
- 默认构造：类中若无手动构造，系统自动提供无形参的默认构造。一旦手动编写，默认消失。
- 构造可重载。

**难点**：this 关键字表示当前对象引用，可在构造中调用自身其他构造 `this(参数)`，但必须在首行。

#### 3.4 static 关键字
- 修饰成员变量：**类变量**，属于类，所有对象共享。
- 修饰成员方法：**类方法**，可直接通过类名调用，内部不能直接访问非静态成员。
- 静态代码块：类加载时执行一次，常用于初始化静态资源。

**重点**：内存中静态变量位于方法区，实例变量位于堆。

#### 3.5 继承
- 格式：`class 子类 extends 父类 {}`
- Java 只支持单继承，但支持多层继承。
- 子类拥有父类非 private 成员。
- 子类可以定义自己独有的属性和行为。

**访问特点**：子类方法中访问变量按就近原则（局部 → 本类成员 → 父类成员）。
**重写（Override）**：子类方法签名与父类完全相同，覆盖父类方法。访问权限不能降低，通常用 `@Override` 注解检查。

#### 3.6 super 关键字
- 表示父类存储空间的标识。
- 调用父类构造：`super(参数)` 必须放在子类构造第一行（默认调用父类无参构造）。
- 访问父类成员变量：`super.成员名`
- 调用父类方法：`super.方法名()`

#### 3.7 多态
- **定义**：同一个行为具有多个不同表现形式。前提：继承/实现 + 方法重写 + 父类引用指向子类对象。
- **格式**：`父类 变量 = new 子类();`
- **特点**：编译看左边（父类），运行看右边（子类）—— 方法调用时运行子类重写的方法。
- **转型**：
  - 向上转型（自动）：子类→父类，多态的形式。
  - 向下转型（强制）：父类→子类，用于调用子类特有方法（需先按原型判断 `instanceof`）。

**经典示例**：
```java
Animal a = new Cat(); // 向上转型
a.eat(); // 调用 Cat 重写的 eat 方法
if (a instanceof Cat) {
    Cat c = (Cat) a; // 向下转型
    c.catchMouse();
}
```

#### 3.8 抽象类与接口
- **抽象类**：`abstract class`，不能实例化；可以包含抽象方法（无方法体）和具体方法。子类必须重写所有抽象方法。
- **接口**：`interface`，比抽象类更纯粹的抽象。JDK 8+ 允许默认方法和静态方法。成员变量默认 `public static final`。一个类可以实现多个接口，弥补单继承不足。
- **区别**：抽象类强调事物的本质共性（is-a），接口强调功能扩展（has-a / can-do）。

#### 3.9 内部类
- **成员内部类**：定义在外部类里面，可以直接访问外部类所有成员。
- **局部内部类**：定义在方法内，作用域仅限方法内。
- **匿名内部类**：是局部内部类的特殊形式，必须继承父类或实现接口，通常用于快速创建一次性对象，多见于事件监听。
  - 格式：`new 父类/接口() { 重写方法 };`

---

### 第4章 Java 常用核心类

#### 4.1 String 类
- 字符串常量，不可变字符序列。
- 创建方式：
  - 直接赋值：`String s = "abc";`（存储在字符串常量池）
  - 构造方法：`String s = new String("abc");`（堆中）
- **常用方法**：
  - `length()`、`charAt(index)`、`indexOf(字符/字串)`、`substring(begin, end)`、`toUpperCase()`、`toLowerCase()`、`trim()`（去空格）、`split(正则)`、`equals()`（比较内容）、`equalsIgnoreCase()`
- **字符串拼接**：频繁拼接推荐使用 `StringBuilder`（线程不安全，效率高）或 `StringBuffer`（线程安全），避免产生大量中间字符串对象。

#### 4.2 包装类
- 基本类型对应引用类型：`byte`→`Byte`，`int`→`Integer`，`char`→`Character` 等。
- **自动装箱/拆箱**（JDK5+）：基本类型与包装类自动转换。
- **应用**：集合中只能存储引用类型，基本类型需转为包装类；字符串转换：`Integer.parseInt("123")`。

#### 4.3 Math 类
- 工具类，提供数学方法：`abs`、`max`、`min`、`pow`、`sqrt`、`random()`（生成 0.0~1.0 随机数）。

#### 4.4 Java 新日期时间 API（java.time 包）
- 传统 `Date`、`Calendar` 的缺陷（可变、偏移量）促使 JDK8 引入新 API。
- **LocalDate**：日期（年-月-日）
- **LocalTime**：时间（时:分:秒）
- **LocalDateTime**：日期+时间
- **常用方法**：`now()`、`of()`、`getYear()`、`plusDays()`、`isAfter()` 等。
- **格式化**：`DateTimeFormatter` 线程安全。

#### 4.5 集合框架
- **Collection** 接口（单列集合）
  - **List**（有序可重复）：`ArrayList`（数组实现，查改快）、`LinkedList`（链表实现，插删快）。
  - **Set**（无序不可重复）：`HashSet`（哈希表，需重写 equals 和 hashCode），`TreeSet`（二叉树，自然排序或比较器排序）。
- **Map** 接口（双列集合，键值对）
  - `HashMap`（哈希表，键唯一，允许 null 键），`TreeMap`（红黑树，键排序）。
- **遍历方式**：
  - List: 普通 for、增强 for、Iterator 迭代器。
  - Set: 增强 for、迭代器。
  - Map: 遍历键集、值集或 entrySet。

**重点**：集合与数组区别：长度可变、存储引用类型、提供丰富的方法。

#### 4.6 泛型
- 将类型参数化，在编译阶段进行类型检查，避免 ClassCastException。
- 格式：`List<String> list = new ArrayList<>();`（后面可省略类型推断）
- 泛型类、泛型方法、泛型接口。
- 通配符 `?`：无界通配、上限 `? extends 类型`（只能取不能存？），下限 `? super 类型`。

---

### 第5章 异常处理与 I/O 基础（扩展）

#### 5.1 异常处理
- **异常体系**：`Throwable` → `Error`（严重错误，不处理）和 `Exception`。
  - `Exception` 分为检查时异常（编译期必须处理，如 IOException、SQLException）和运行时异常（RuntimeException 及子类，如空指针、下标越界，代码不规范引发）。
- **处理方式**：
  - try-catch：尝试捕获一个或多个异常。
  - finally：无论是否异常都执行的代码，用于释放资源。
  - throws：方法声明抛出异常，抛给调用者。
  - throw：手动抛出异常对象。
- **自定义异常**：继承 Exception 或 RuntimeException，调用 super 方法传递错误信息。

#### 5.2 File 类与 I/O 概述
- `File` 表示文件或目录路径的抽象，可进行创建、删除、重命名、判断存在等操作。
- **流分类**：
  - 字节流（以 byte 为单位）：`InputStream` / `OutputStream`，常用于非文本文件。
  - 字符流（以字符为单位）：`Reader` / `Writer`，用于文本。
- **常见流**：
  - 文件字节流：`FileInputStream` / `FileOutputStream`
  - 缓冲字符流：`BufferedReader` / `BufferedWriter`（高效）
- 流使用后必须 close，推荐 try-with-resources（自动关闭）。

---

## 四、课后习题

1. **简答题**  
   简述 JVM、JRE、JDK 三者的关系，并说明各自的作用。

   ***参考答案***：  
   JVM 是 Java 虚拟机，负责解释执行字节码；JRE 是 Java 运行环境，包含 JVM 和运行所需的核心类库；JDK 是 Java 开发工具包，包含 JRE 以及编译器、调试器等开发工具。开发时安装 JDK，运行时只需 JRE。

2. **代码分析题**  
   下列代码输出结果是什么？为什么？
   ```java
   String s1 = "hello";
   String s2 = "hello";
   String s3 = new String("hello");
   System.out.println(s1 == s2);
   System.out.println(s1 == s3);
   ```
   ***参考答案***：  
   输出 true 和 false。s1 与 s2 指向常量池同一对象，所以 == 比较引用地址为 true；s3 是在堆中新建的对象，地址不同，结果为 false。要比较内容应用 `equals()`。

3. **编程题**  
   定义一个类 `Circle`，包含私有属性半径 `radius`，提供无参和有参构造方法，set/get 方法，计算面积的方法 `getArea()`（PI * radius * radius）。在 main 方法中创建对象并调用方法测试。

   ***参考代码***（核心部分）：
   ```java
   public class Circle {
       private double radius;
       public Circle() {}
       public Circle(double radius) { this.radius = radius; }
       public void setRadius(double radius) { this.radius = radius; }
       public double getRadius() { return radius; }
       public double getArea() { return Math.PI * radius * radius; }
   }
   ```

4. **多态应用题**  
   现有父类 `Animal`，子类 `Dog`、`Cat` 均重写了 `sound()` 方法。请编写代码演示多态：将 Dog 和 Cat 对象存入一个 Animal 类型的数组，并循环调用 sound() 方法。

   ***参考代码***：
   ```java
   Animal[] animals = {new Dog(), new Cat()};
   for (Animal a : animals) {
       a.sound();
   }
   ```

5. **集合应用题**  
   使用 HashMap 存储学生姓名与成绩，遍历成绩单，并找出最高分的学生。

   ***参考思路***：创建 `Map<String, Integer>`，用 `entrySet()` 遍历，在循环内比较 value 取得最大值及对应 key。

---

## 五、参考资料

由于本次未提供外部搜索结果，以下推荐常用学习资源，供学生课后拓展：

- **官方文档**  
  Oracle Java SE Documentation：https://docs.oracle.com/en/java/javase/  
  （涵盖类库详细说明与示例）

- **经典书籍**  
  - 《Head First Java》（第3版）—— 图文并茂，适合零基础
  - 《Java 核心技术 卷I：基础知识》（第11版）—— 系统全面，大学教材常用
  - 《Effective Java》（第3版）—— 进阶最佳实践

- **在线学习平台**  
  - 菜鸟教程 Java 基础：https://www.runoob.com/java/  
  - B站黑马程序员 Java 入门教程（免费视频）
  - MOOC 网《零基础学Java语言》—— 翁恺

- **开发工具**  
  - IntelliJ IDEA 社区版（免费，推荐）  
  - Eclipse IDE for Java Developers

以上资源可辅助课堂教学，加深对 Java 基础知识的理解和应用。

---

**课件编写说明**：本课件依据 Java SE 核心知识点系统整理，内容涵盖从环境搭建到面向对象及常用类库，难度适中，适用于高职及本科阶段教学。各部分可根据实际课时灵活调整详略，并配合课堂演示与上机练习巩固。