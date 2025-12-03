-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 30 Nov 2025 pada 12.22
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `ets_mbg`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `bahan_baku`
--

CREATE TABLE `bahan_baku` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `nama` varchar(120) DEFAULT NULL COMMENT 'Nama Bahan',
  `kategori` varchar(60) DEFAULT NULL COMMENT 'Kategori Bahan',
  `jumlah` int(11) DEFAULT NULL COMMENT 'Stok Tersedia',
  `satuan` varchar(20) DEFAULT NULL COMMENT 'Satuan Bahan',
  `tanggal_masuk` date DEFAULT NULL,
  `tanggal_kadaluarsa` date DEFAULT NULL,
  `status` enum('tersedia','segera_kadaluarsa','kadaluarsa','habis') DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL COMMENT 'Waktu Dibuat'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data untuk tabel `bahan_baku`
--

INSERT INTO `bahan_baku` (`id`, `nama`, `kategori`, `jumlah`, `satuan`, `tanggal_masuk`, `tanggal_kadaluarsa`, `status`, `created_at`) VALUES
(1, 'Beras Medium', 'Karbohidrat', 212, 'kg', '2025-11-20', '2026-03-01', 'tersedia', '2025-11-07 00:23:25'),
(2, 'Telur Ayam', 'Protein Hewani', 1960, 'butir', '2025-11-20', '2025-10-10', 'kadaluarsa', '2025-10-30 00:23:25'),
(3, 'Daging Ayam Broiler', 'Protein Hewani', 197, 'kg', '2025-11-20', '2025-10-02', 'kadaluarsa', '2025-10-31 00:23:25'),
(4, 'Tahu Putih', 'Protein Nabati', 400, 'potong', '2025-11-20', '2025-10-01', 'kadaluarsa', '2025-10-25 00:23:25'),
(5, 'Tempe', 'Protein Nabati', 295, 'potong', '2025-11-20', '2025-10-03', 'kadaluarsa', '2025-11-11 00:23:25'),
(6, 'Sayur Bayam', 'Sayuran', 50, 'ikat', '2025-11-20', '2025-10-01', 'kadaluarsa', '2025-11-09 00:23:25'),
(7, 'Wortel', 'Sayuran', 80, 'kg', '2025-11-20', '2025-10-15', 'kadaluarsa', '2025-10-23 00:23:25'),
(8, 'Kentang', 'Karbohidrat', 95, 'kg', '2025-11-20', '2025-11-20', 'kadaluarsa', '2025-11-13 00:23:25'),
(9, 'Minyak Goreng Sawit', 'Bahan Masak', 79, 'liter', '2025-11-20', '2026-01-01', 'tersedia', '2025-11-18 00:23:25'),
(10, 'Susu Bubuk Fortifikasi', 'Protein Hewani', 40, 'kg', '2025-11-20', '2025-12-10', 'tersedia', '2025-11-04 00:23:25');

-- --------------------------------------------------------

--
-- Struktur dari tabel `migrations`
--

CREATE TABLE `migrations` (
  `id` int(10) UNSIGNED NOT NULL,
  `migration` varchar(255) NOT NULL,
  `batch` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data untuk tabel `migrations`
--

INSERT INTO `migrations` (`id`, `migration`, `batch`) VALUES
(1, '2025_10_04_032537_user', 1),
(2, '2025_10_04_032546_bahan__baku', 1),
(3, '2025_10_04_032557_permintaan', 1),
(4, '2025_10_04_032602_permintaan__detail', 1);

-- --------------------------------------------------------

--
-- Struktur dari tabel `permintaan`
--

CREATE TABLE `permintaan` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `pemohon_id` bigint(20) UNSIGNED NOT NULL,
  `tgl_masak` date NOT NULL COMMENT 'Tanggal rencana memasak',
  `menu_makan` varchar(255) NOT NULL COMMENT 'Deskripsi Menu',
  `jumlah_porsi` int(11) NOT NULL,
  `status` enum('menunggu','disetujui','ditolak') NOT NULL COMMENT 'Status Permintaan',
  `created_at` timestamp NULL DEFAULT NULL COMMENT 'Waktu Dibuat'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data untuk tabel `permintaan`
--

INSERT INTO `permintaan` (`id`, `pemohon_id`, `tgl_masak`, `menu_makan`, `jumlah_porsi`, `status`, `created_at`) VALUES
(1, 6, '2025-09-30', 'Nasi ayam goreng + sayur bayam', 200, 'disetujui', '2025-09-28 03:00:00'),
(2, 7, '2025-09-30', 'Tempe goreng + sayur wortel', 150, 'disetujui', '2025-09-28 03:05:00'),
(3, 8, '2025-10-01', 'Nasi + ayam rebus + bayam bening', 180, 'disetujui', '2025-09-29 03:10:00'),
(4, 9, '2025-10-01', 'Kentang balado + telur rebus', 120, 'disetujui', '2025-09-29 03:15:00'),
(5, 10, '2025-10-02', 'Nasi tempe orek + sayur sop', 200, 'disetujui', '2025-09-29 03:20:00'),
(6, 6, '2025-10-02', 'Ayam goreng tepung + wortel kukus', 220, 'ditolak', '2025-09-29 03:25:00'),
(7, 7, '2025-10-03', 'Nasi telur dadar + bayam bening', 180, 'disetujui', '2025-09-30 03:30:00'),
(8, 8, '2025-10-03', 'Kentang rebus + ayam suwir', 160, 'disetujui', '2025-09-30 03:35:00'),
(9, 9, '2025-10-04', 'Nasi + tempe goreng + sayur bening', 190, 'disetujui', '2025-09-30 03:40:00'),
(10, 10, '2025-10-04', 'Sup ayam + susu fortifikasi', 210, 'disetujui', '2025-09-30 03:45:00'),
(11, 6, '2025-11-21', 'Nasi Goreng', 2, 'disetujui', '2025-11-20 07:57:24'),
(12, 6, '2025-11-22', 'NasGor Goren', 2, 'disetujui', '2025-11-21 01:01:30'),
(13, 6, '2025-11-28', 'kuda rebus', 1, 'disetujui', '2025-11-27 06:50:17'),
(14, 6, '2025-12-01', 'Raihana Goreng', 3, 'menunggu', '2025-11-30 11:20:08');

-- --------------------------------------------------------

--
-- Struktur dari tabel `permintaan_detail`
--

CREATE TABLE `permintaan_detail` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `permintaan_id` bigint(20) UNSIGNED NOT NULL,
  `bahan_id` bigint(20) UNSIGNED DEFAULT NULL,
  `jumlah_diminta` int(11) NOT NULL COMMENT 'Jumlah bahan diminta'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data untuk tabel `permintaan_detail`
--

INSERT INTO `permintaan_detail` (`id`, `permintaan_id`, `bahan_id`, `jumlah_diminta`) VALUES
(1, 1, 1, 50),
(2, 1, 3, 40),
(3, 1, 6, 50),
(4, 2, 1, 40),
(5, 2, 5, 30),
(6, 2, 7, 20),
(7, 3, 1, 45),
(8, 3, 3, 30),
(9, 3, 6, 40),
(10, 4, 1, 30),
(11, 4, 8, 20),
(12, 4, 2, 60),
(13, 5, 1, 60),
(14, 5, 5, 30),
(15, 5, 7, 20),
(16, 6, 1, 50),
(17, 6, 3, 50),
(18, 7, 1, 40),
(19, 7, 2, 40),
(20, 7, 6, 30),
(21, 8, 1, 35),
(22, 8, 8, 25),
(23, 8, 3, 20),
(24, 9, 1, 45),
(25, 9, 5, 25),
(26, 9, 6, 30),
(27, 10, 1, 60),
(28, 10, 3, 50),
(29, 10, 10, 10),
(30, 12, 1, 1),
(31, 12, 9, 1),
(32, 13, 1, 2),
(33, 13, 3, 3);

-- --------------------------------------------------------

--
-- Struktur dari tabel `user`
--

CREATE TABLE `user` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password` varchar(255) NOT NULL COMMENT 'Hashed Password',
  `role` enum('gudang','dapur') NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL COMMENT 'Waktu Dibuat'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data untuk tabel `user`
--

INSERT INTO `user` (`id`, `name`, `email`, `password`, `role`, `created_at`) VALUES
(1, 'Budi Santoso', 'budi.gudang@mbg.id', '$2y$12$6EnkLqbPiD4VsAJE4GGBmeH8WCTZIw/04pg0OuEyC/hCr0AWZutEq', 'gudang', NULL),
(2, 'Siti Aminah', 'siti.gudang@mbg.id', '$2y$12$G0gc01TUobM1c9PHSoRY1emC/WCJrNhCsJ6vewV.0M/ApgB9DG6fe', 'gudang', NULL),
(3, 'Rahmat Hidayat', 'rahmat.gudang@mbg.id', '$2y$12$khS8TJQxtIkW4dgZ4d0HMuicUaPqFy6wfQWnQa/8aPIc9MtpC44Ji', 'gudang', NULL),
(4, 'Lina Marlina', 'lina.gudang@mbg.id', '$2y$12$h8oV/qS5IBrLMf7tydFzdOpGFrjS6VHMk.sZxOZ/sr3F68zSqoSjS', 'gudang', NULL),
(5, 'Anton Saputra', 'anton.gudang@mbg.id', '$2y$12$t.zcP7bN9/KkNd6cA/MEc.VXrzmERX9oM3qIpLt6tggaNKxNMP5cy', 'gudang', NULL),
(6, 'Dewi Lestari', 'dewi.dapur@mbg.id', '$2y$12$UAeuBfdK7x67uTdG1DIiLuAXM6XRuS7IOg5v7S.4EW2H9us0T.Ag.', 'dapur', NULL),
(7, 'Andi Pratama', 'andi.dapur@mbg.id', '$2y$12$wCxrR9S2xhOfnB6nhYYqru0rdmFkCtuaW160EWTCjjN7VHYdywpHe', 'dapur', NULL),
(8, 'Maria Ulfa', 'maria.dapur@mbg.id', '$2y$12$0wbITzTsZLJ1EAvcSwjioOOU20UmvRIZnSdZwemMu4n9KeFFykDh.', 'dapur', NULL),
(9, 'Surya Kurnia', 'surya.dapur@mbg.id', '$2y$12$r8XcfQLVHWIg3QTVQmfnAOqUTnoa1CF8BiYO9HcV9rILV/8potmCe', 'dapur', NULL),
(10, 'Yanti Fitri', 'yanti.dapur@mbg.id', '$2y$12$VpdF/PrAXdto2ubqxitNKu6ucicjVshvhQNleezcB4ns6Fab.v4ha', 'dapur', NULL);

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `bahan_baku`
--
ALTER TABLE `bahan_baku`
  ADD PRIMARY KEY (`id`);

--
-- Indeks untuk tabel `migrations`
--
ALTER TABLE `migrations`
  ADD PRIMARY KEY (`id`);

--
-- Indeks untuk tabel `permintaan`
--
ALTER TABLE `permintaan`
  ADD PRIMARY KEY (`id`),
  ADD KEY `permintaan_pemohon_id_foreign` (`pemohon_id`);

--
-- Indeks untuk tabel `permintaan_detail`
--
ALTER TABLE `permintaan_detail`
  ADD PRIMARY KEY (`id`),
  ADD KEY `permintaan_detail_permintaan_id_foreign` (`permintaan_id`),
  ADD KEY `permintaan_detail_bahan_id_foreign` (`bahan_id`);

--
-- Indeks untuk tabel `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_name_unique` (`name`),
  ADD UNIQUE KEY `user_email_unique` (`email`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `bahan_baku`
--
ALTER TABLE `bahan_baku`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT untuk tabel `migrations`
--
ALTER TABLE `migrations`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT untuk tabel `permintaan`
--
ALTER TABLE `permintaan`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT untuk tabel `permintaan_detail`
--
ALTER TABLE `permintaan_detail`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- AUTO_INCREMENT untuk tabel `user`
--
ALTER TABLE `user`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `permintaan`
--
ALTER TABLE `permintaan`
  ADD CONSTRAINT `permintaan_pemohon_id_foreign` FOREIGN KEY (`pemohon_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Ketidakleluasaan untuk tabel `permintaan_detail`
--
ALTER TABLE `permintaan_detail`
  ADD CONSTRAINT `permintaan_detail_bahan_id_foreign` FOREIGN KEY (`bahan_id`) REFERENCES `bahan_baku` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `permintaan_detail_permintaan_id_foreign` FOREIGN KEY (`permintaan_id`) REFERENCES `permintaan` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
