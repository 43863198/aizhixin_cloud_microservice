package com.aizhixin.cloud.dd;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimaps;
import org.apache.commons.lang.StringUtils;

public class TestG {

    public static void main(String[] args) {
        String str = "";

// list转字符串
        List<String> list = new ArrayList<String>();
        list.add("A");
        list.add("B");
        list.add("C");
        str = StringUtils.join(list.toArray(), ","); // 根据不同切割符返回字符串
        System.out.println("正常List转换：" + str);

//        TestG g = new TestG();
//        g.testTest6();
    }

    public void testTest6() {
        Person p1 = new Person("a", 10, "男", "china");
        Person p2 = new Person("b", 11, "女", "japan");
        Person p3 = new Person("c", 12, "男", "japan");
        Person p4 = new Person("d", 14, "男", "USA");
        Person p5 = new Person("e", 14, "女", "china");
        List<Person> persons = new ArrayList<>();
        persons.add(p1);
        persons.add(p2);
        persons.add(p3);
        persons.add(p4);
        persons.add(p5);
        ImmutableSet digits = ImmutableSet.of(persons.toArray());
        Function<Person, String> sex = new Function<Person, String>() {
            @Override
            public String apply(Person person) {
                return person.getSex();
            }
        };
        ImmutableListMultimap<String, Person> sexList = Multimaps.index(digits,
                sex);

        ImmutableListMultimap ageList = Multimaps.index(digits,
                new Function<Person, Integer>() {
                    @Override
                    public Integer apply(Person person) {
                        return person.getAge();
                    }
                });

        ImmutableListMultimap countryList = Multimaps.index(digits,
                new Function<Person, String>() {
                    @Override
                    public String apply(Person person) {
                        return person.getContry();
                    }
                });
        System.out.println("按性别分组 = " + sexList);
        System.out.println("按年龄分组 = " + ageList);
        System.out.println("按国家分组 = " + countryList);

        /**
         * 结果 按性别分组 = {男=[Person@44d03877, Person@215750e4, Person@6b7fb9d5],
         * 女=[Person@422b2fec, Person@e818616]} 按年龄分组 = {10=[Person@44d03877],
         * 11=[Person@422b2fec], 12=[Person@215750e4], 14=[Person@6b7fb9d5,
         * Person@e818616]} 按国家分组 = {china=[Person@44d03877, Person@e818616],
         * japan=[Person@422b2fec, Person@215750e4], USA=[Person@6b7fb9d5]}
         */
        // 求性别为男的集合
        System.out.println("sexList = " + sexList.get("男"));
        // 求年龄为14岁的集合
        System.out.println("ageList = " + ageList.get(14));
        // 求国籍日本的集合
        System.out.println("countryList = " + countryList.get("japan"));
        /**
         * 结果 sexList = [Person@e818616, Person@789caeb2, Person@769165fa]
         * ageList = [Person@769165fa, Person@43be3ce6] countryList =
         * [Person@598a15ca, Person@789caeb2] 以上结果就是根据返回的map得到的集合数据。
         * 可以把结果用List<Person> 保存。 List<Person> personMan = sexList.get("男");
         */
    }

    class Person {
        String contry;
        int age;
        String sex;
        String name;

        public Person(String contry, int age, String sex, String name) {
            super();
            this.contry = contry;
            this.age = age;
            this.sex = sex;
            this.name = name;
        }

        public String getContry() {
            return contry;
        }

        public void setContry(String contry) {
            this.contry = contry;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
}
