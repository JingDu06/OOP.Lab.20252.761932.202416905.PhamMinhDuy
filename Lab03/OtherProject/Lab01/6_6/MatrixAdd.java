import java.util.Scanner;

public class MatrixAdd {

    public static void main(String[] args){
        Scanner keyboard = new Scanner(System.in);

        int[][] matrice1 = new int[100][100];
        int[][] matrice2 = new int[100][100];

        System.out.print("Nhap so hang cua ma tran: ");
        int n = keyboard.nextInt();
        System.out.print("Nhap so cot cua ma tran: ");
        int m = keyboard.nextInt();

        System.out.println("Nhap ma tran 1 nao: ");
        for(int i = 0;i < n;i++)
            for(int j = 0;j < m;j++)
                matrice1[i][j] = keyboard.nextInt();
        System.out.println("Nhap ma tran 2 nao: ");
        for(int i = 0;i < n;i++)
            for(int j = 0;j < m;j++)
                matrice2[i][j] = keyboard.nextInt();

        int[][] result = new int[100][100];
        for(int i = 0;i < n;i++)
            for(int j = 0;j < m;j++)
                result[i][j] = matrice1[i][j] + matrice2[i][j];
        System.out.println("Ket qua cua ban la: ");
        for(int i = 0;i < n;i++) {
            for (int j = 0; j < m; j++) {
                System.out.print(result[i][j] + " ");
            }
            System.out.print("\n");
        }
        System.exit(0);

    }
}