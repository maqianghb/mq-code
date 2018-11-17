package com.example.mq.service.leedcode;

import java.util.HashMap;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-07-28 16:31:09
 **/
public class LinkedListExercise {

    public static void main(String[] args){

        ListNode head =new ListNode(1);
        ListNode tmp =head;
        tmp.next =new ListNode(2);
        tmp =tmp.next;
        tmp.next =new ListNode(3);
        tmp =tmp.next;
        tmp.next =new ListNode(4);
        tmp =tmp.next;
        tmp.next =new ListNode(5);
        tmp =tmp.next;
        tmp.next =new ListNode(6);

        LinkedListExercise exercise = new LinkedListExercise();
        TreeNode node =exercise.sortedListToBST(head);
//        exercise.printListValue(node);


        HashMap<String, Object> testMap =new HashMap<String, Object>();


    }

    //检测是否有环
    //思路：快慢指针
    public ListNode detectCycle(ListNode head) {
        if(null ==head || null ==head.next){
            return null;
        }
        ListNode fast =head;
        ListNode slow =head;
        while(null !=fast.next && null != fast.next.next){
            slow =slow.next;
            fast =fast.next.next;
            if(fast ==slow){
                break;
            }
        }
        if(null ==fast.next || null ==fast.next.next){
            return null;
        }
        slow =head;
        while(slow !=fast){
            slow =slow.next;
            fast =fast.next;
        }
        return slow;
    }

    //链表转二叉树
    //思路：找到中间节点，作为根节点，递归处理左右子树
    public TreeNode sortedListToBST(ListNode head) {
        if( null ==head){
            return null;
        }else if(null ==head.next){
            return new TreeNode(head.val);
        }
        ListNode middle =findMiddle(head);
        TreeNode root =new TreeNode(middle.val);
        root.left =sortedListToBST(head);
        root.right =sortedListToBST(middle.next);
        return root;
    }

    private ListNode findMiddle(ListNode head){
        if(null ==head || null ==head.next){
            return head;
        }
        ListNode step1 =head;
        ListNode step2 =head;
        ListNode prev = new ListNode(0);
        prev.next =step1;
        while(null !=step2.next && null !=step2.next.next){
            prev =prev.next;
            step1 =step1.next;
            step2 =step2.next.next;
        }
        //分割成两段
        prev.next =null;
        return step1;
    }

    //反转链表
    //思路：构造新链表放m和n之间元素
    public ListNode reverseBetween(ListNode head, int m, int n) {
        if(null ==head || null ==head.next || m>n){
            return head;
        }

        ListNode prev =new ListNode(0);
        prev.next =head;
        ListNode start =prev;
        for(int i=0;i<m-1 && null !=prev.next ;i++){
            prev =prev.next;
        }
        ListNode reverseNode =new ListNode(0);
        for(int i=0; i<n-m+1 && null !=prev.next;i++){
            ListNode tmp = prev.next;
            prev.next =tmp.next;
            tmp.next =reverseNode.next;
            reverseNode.next =tmp;
        }
        ListNode endNode =reverseNode;
        while(null !=endNode.next){
            endNode =endNode.next;
        }

        endNode.next =prev.next;
        prev.next =reverseNode.next;
        return start.next;
    }


    //分割链表
    //思路：两个链表，一个放小于x的节点，另一个放大于x的节点
    public ListNode partition(ListNode head, int x) {
        if(null ==head || null ==head.next){
            return head;
        }
        ListNode small =new ListNode(0);
        ListNode smallStart =small;
        ListNode big = new ListNode(0);
        ListNode bigStart =big;

        ListNode tmp =head;
        while(null != tmp){
            if(tmp.val <x){
                small.next =tmp;
                small =small.next;
            }else{
                big.next =tmp;
                big =big.next;
            }
            tmp =tmp.next;
        }
        small.next =bigStart.next;
        big.next =null;
        return smallStart.next;
    }


    //删除链表中重复元素
    //思路：找到重复节点的最后一个
    public ListNode deleteDuplicates(ListNode head) {
        if(null ==head || null ==head.next ){
            return head;
        }
        ListNode prev =new ListNode(0);
        prev.next =head;
        ListNode start =prev;

        while(null !=prev.next){
            ListNode tmpNode =prev.next;
            while(null !=tmpNode.next && tmpNode.val == tmpNode.next.val){
                tmpNode =tmpNode.next;
            }
            if(prev.next !=tmpNode){
                prev.next =tmpNode.next;
            }else{
                //确认tmp和prev值不一样，才能将prev后挪一位
                prev =prev.next;
            }
        }
        return start.next;
    }

    //旋转链表
    //思路：计算链表长度和右移位数，构造循环链表计算新的头节点
    //注：与删除倒数第n个节点类似
    public ListNode rotateRight(ListNode head, int k) {
        if(null ==head){
            return head;
        }
        ListNode tmp =head;
        int length =1;
        while(null !=tmp.next){
            tmp =tmp.next;
            length++;
        }
        tmp.next =head;
        int moveNum =k % length;
        //注意计算尾节点位置
        ListNode tail =head;
        for(int i=0; i<length -moveNum-1;i++){
            tail =tail.next;
        }
        head =tail.next;
        tail.next =null;
        return head;
    }

    //两两交换节点
    //思路：三个标识，交换的节点及前一个节点
    public ListNode swapPairs(ListNode head) {
        ListNode tmp =new ListNode(0);
        tmp.next =head;
        ListNode newHead =tmp;

        while(null !=tmp.next && null!=tmp.next.next){
            ListNode p1=tmp.next;
            ListNode p2=tmp.next.next;
            //swap
            p1.next =p2.next;
            p2.next =p1;
            tmp.next =p2;
            //next tmp
            tmp =p1;
        }
        return newHead.next;
    }

    //删除倒数第n个节点
    //思路：两个节点相差n，当一个恰好走到最后一个节点，另一个则为倒数第n+1个
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode p1 =head;
        ListNode p2 =head;
        for(int i=0;i<n ; i++){
            if(null !=p1.next){
                p1 =p1.next;
            }else{
                return head.next;
            }

        }
        while(null !=p1.next){
            p1 =p1.next;
            p2 =p2.next;
        }
        p2.next =p2.next.next;
        return head;
    }

    static class ListNode{
        private ListNode next;
        private int val;

        public ListNode(int val){
            this.val = val;
            this.next =null;
        }
    }

    static class TreeNode{
        int val;
        TreeNode left;
        TreeNode right;

        public TreeNode(int val){
            this.val =val;
            this.left =null;
            this.right =null;
        }
    }

    public void printListValue(ListNode head){
        while(null !=head){
            System.out.println(head.val);
            head =head.next;
        }
    }
}
