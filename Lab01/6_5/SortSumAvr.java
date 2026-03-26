import java.util.Scanner;

public class SortSumAvr {
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        int[] a = new int[101];
        int n; // số phần tử
        System.out.println("Nhap so phan tu cua day: ");
        n = keyboard.nextInt();
        for (int i = 0; i < n; i++) {
            System.out.print("Nhap phan tu thu nhat: ");
            a[i] = keyboard.nextInt();
        }
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (a[i] > a[j]) {
                    int tmp = a[i];
                    a[i] = a[j];
                    a[j] = tmp;
                }
            }
        }

        System.out.print("Mang da sap xep: ");
        for (int i = 0; i < n; i++) {
            System.out.print(a[i] + " ");
        }
        System.out.println();
        double sum = 0;
        for(int i=0; i<n; i++) {
        	sum += a[i];
        }
        double avg = sum/n;
        System.out.println("Sum of elements in array: "+ sum);
        System.out.println("Average element: "+ avg);
    }
}