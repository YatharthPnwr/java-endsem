-- Database schema for Event Aggregator
-- This script creates the necessary tables and inserts sample data

-- Create database if it doesn't exist 
CREATE DATABASE IF NOT EXISTS eventWebApp;

-- Use the database
USE eventWebApp;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    password VARCHAR(100) NOT NULL,
    user_type ENUM('CUSTOMER', 'VENDOR', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Vendors table
CREATE TABLE IF NOT EXISTS vendors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    business_name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    service_type VARCHAR(50) NOT NULL,
    base_cost DECIMAL(10, 2) NOT NULL,
    location VARCHAR(100) NOT NULL,
    contact_email VARCHAR(100) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    rating DECIMAL(3, 2) DEFAULT 0.0,
    image_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Cart items table
CREATE TABLE IF NOT EXISTS cart_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    vendor_id INT NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE,
    UNIQUE (user_id, vendor_id)
);

-- Bookings table
CREATE TABLE IF NOT EXISTS bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    event_date DATE NOT NULL,
    event_location VARCHAR(100) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    total_cost DECIMAL(10, 2) NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Booking vendors table (many-to-many relationship between bookings and vendors)
CREATE TABLE IF NOT EXISTS booking_vendors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    vendor_id INT NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE,
    UNIQUE (booking_id, vendor_id)
);

-- Reviews table
CREATE TABLE IF NOT EXISTS reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    vendor_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE
);

-- Create table for vendor services
CREATE TABLE IF NOT EXISTS vendor_services (
    id INT AUTO_INCREMENT PRIMARY KEY,
    vendor_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    service_type VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    location VARCHAR(100) NOT NULL,
    image_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE
);

-- Replace all sample data with our new data
-- Clean up existing data first
DELETE FROM booking_vendors;
DELETE FROM bookings;
DELETE FROM cart_items;
DELETE FROM reviews;
DELETE FROM vendor_services;
DELETE FROM vendors;
DELETE FROM users;

-- Create user types - 1 customer and several vendors
INSERT INTO users (id, name, email, password, user_type, phone) VALUES
-- Customer account
(1, 'Rakesh Patil', 'rakesh@example.com', 'password123', 'CUSTOMER', '555-123-4567'),
-- Vendor accounts
(2, 'Elegant Venues Inc.', 'info@elegantvenuesinc.com', 'vendor123', 'VENDOR', '555-234-5678'),
(3, 'Delicious Catering', 'contact@deliciouscatering.com', 'vendor123', 'VENDOR', '555-345-6789'),
(4, 'Perfect Moments Photography', 'hello@perfectmomentsphotos.com', 'vendor123', 'VENDOR', '555-456-7890'),
(5, 'Floral Designs', 'orders@floraldesigns.com', 'vendor123', 'VENDOR', '555-567-8901'),
(6, 'Musical Entertainment', 'bookings@musicalentertainment.com', 'vendor123', 'VENDOR', '555-678-9012'),
(7, 'Luxury Transport', 'reservations@luxurytransport.com', 'vendor123', 'VENDOR', '555-789-0123'),
(8, 'Dream Decor', 'info@dreamdecor.com', 'vendor123', 'VENDOR', '555-890-1234'),
(9, 'Signature Cakes', 'orders@signaturecakes.com', 'vendor123', 'VENDOR', '555-901-2345'),
(10, 'Divine Wedding Planner', 'events@divineplanners.com', 'vendor123', 'VENDOR', '555-012-3456'),
(11, 'Sound & Light Pro', 'tech@soundlightpro.com', 'vendor123', 'VENDOR', '555-123-4567');

-- Create 10 vendors across 4 categories: Venue, Catering, Photography, Decoration
INSERT INTO vendors (id, user_id, business_name, service_type, description, contact_email, contact_phone, location, base_cost, image_url, is_active, rating) VALUES
-- Venue vendors (3)
(1, 2, 'Royal Palace Banquet', 'Venue', 'Luxurious banquet hall with capacity for 500 guests, featuring elegant décor, state-of-the-art sound system, and a beautiful outdoor garden for ceremonies. Perfect for weddings and large corporate events.', 'info@elegantvenuesinc.com', '555-234-5678', 'Vadodara, Gujarat', 50000.00, 'https://images.unsplash.com/photo-1519167758481-83f550bb49b3?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80', true, 4.8),

(2, 6, 'Seaside Resort', 'Venue', 'Beautiful beachfront venue with panoramic views of the ocean. Featuring indoor and outdoor spaces, accommodating up to 200 guests with luxury accommodation options for out-of-town guests.', 'bookings@musicalentertainment.com', '555-678-9012', 'Mumbai, Maharashtra', 75000.00, 'https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80', true, 4.7),

(3, 7, 'Heritage Mansion', 'Venue', 'Historic mansion with traditional architecture and modern amenities. Features lush gardens, vintage interiors, and capacity for intimate gatherings of up to 150 guests. Popular for themed events and traditional weddings.', 'reservations@luxurytransport.com', '555-789-0123', 'Ahmedabad, Gujarat', 45000.00, 'https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80', true, 4.5),

-- Catering vendors (3)
(4, 3, 'Grand Feast Catering', 'Catering', 'Premium catering service specializing in multi-cuisine offerings including Indian, Continental, and Oriental dishes. Known for elegant presentation and exceptional taste. Can handle events of all sizes.', 'contact@deliciouscatering.com', '555-345-6789', 'Vadodara, Gujarat', 1200.00, 'https://images.unsplash.com/photo-1555244162-803834f70033?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80', true, 4.9),

(5, 9, 'Sweet Celebrations Bakery', 'Catering', 'Specializing in custom cakes, dessert tables, and sweet treats for all occasions. Offers gluten-free and vegan options. Known for intricate designs and delicious flavors.', 'orders@signaturecakes.com', '555-901-2345', 'Surat, Gujarat', 800.00, 'https://images.unsplash.com/photo-1535141192574-5d4897c12636?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80', true, 4.6),

-- Photography vendors (2)
(6, 4, 'Captured Moments Studio', 'Photography', 'Award-winning photography team specializing in candid wedding photography, pre-wedding shoots, and event coverage. Includes drone photography and same-day edited highlights.', 'hello@perfectmomentsphotos.com', '555-456-7890', 'Vadodara, Gujarat', 25000.00, 'https://images.unsplash.com/photo-1520854221256-17451cc331bf?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80', true, 4.8),

(7, 10, 'Timeless Frames', 'Photography', 'Boutique photography service focusing on artistic composition and storytelling. Offers photography and videography packages including cinematic wedding films and documentary-style event coverage.', 'events@divineplanners.com', '555-012-3456', 'Ahmedabad, Gujarat', 35000.00, 'https://images.unsplash.com/photo-1452587925148-ce544e77e70d?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80', true, 4.7),

-- Decoration vendors (2)
(8, 5, 'Blossom Decor', 'Decoration', 'Specializing in floral arrangements and themed decorations for all occasions. From minimalist modern designs to extravagant floral installations, customized to match your events color scheme and theme.', 'orders@floraldesigns.com', '555-567-8901', 'Vadodara, Gujarat', 18000.00, 'https://images.unsplash.com/photo-1519167758481-83f550bb49b3?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80', true, 4.6),

(9, 8, 'Enchanted Spaces', 'Decoration', 'Transforming ordinary venues into magical spaces with custom lighting, draping, centerpieces, and props. Known for their attention to detail and innovative designs for weddings and corporate events.', 'info@dreamdecor.com', '555-890-1234', 'Delhi, NCR', 22000.00, 'https://images.unsplash.com/photo-1507504031003-b417219a0fde?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80', true, 4.5),

-- Entertainment vendor (1)
(10, 11, 'Rhythm Masters', 'Entertainment', 'Professional DJ services, live bands, and sound equipment rental for events. Includes custom playlists, MC services, dance floor lighting, and sound technicians to ensure perfect acoustics.', 'tech@soundlightpro.com', '555-123-4567', 'Mumbai, Maharashtra', 15000.00, 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80', true, 4.4);

-- Create some sample vendor services for each vendor
INSERT INTO vendor_services (vendor_id, name, description, service_type, price, location, image_url) VALUES
-- Venue vendor services
(1, 'Full Day Venue Rental', 'Complete access to the banquet hall and gardens for a full day event', 'Venue', 50000.00, 'Vadodara, Gujarat', 'https://images.unsplash.com/photo-1519167758481-83f550bb49b3?ixlib=rb-1.2.1&auto=format&fit=crop&w=600&q=80'),
(1, 'Half Day Venue Rental', 'Access to the banquet hall and gardens for 6 hours', 'Venue', 30000.00, 'Vadodara, Gujarat', 'https://images.unsplash.com/photo-1519167758481-83f550bb49b3?ixlib=rb-1.2.1&auto=format&fit=crop&w=600&q=80'),

(2, 'Beach Wedding Package', 'Beachfront ceremony setup with chairs and archway', 'Venue', 75000.00, 'Mumbai, Maharashtra', 'https://images.unsplash.com/photo-1545569331-9a8a3521b4ce?ixlib=rb-1.2.1&auto=format&fit=crop&w=600&q=80'),
(2, 'Indoor Reception', 'Elegant indoor reception venue with sea view', 'Venue', 50000.00, 'Mumbai, Maharashtra', 'https://images.unsplash.com/photo-1545569331-9a8a3521b4ce?ixlib=rb-1.2.1&auto=format&fit=crop&w=600&q=80'),

-- Catering vendor services
(4, 'Premium Buffet', 'Extensive buffet menu with 5 starters, 8 main courses, and 3 desserts', 'Catering', 1200.00, 'Vadodara, Gujarat', 'https://images.unsplash.com/photo-1555244162-803834f70033?ixlib=rb-1.2.1&auto=format&fit=crop&w=600&q=80'),
(4, 'Live Stations', 'Interactive food stations with chefs preparing dishes on demand', 'Catering', 1500.00, 'Vadodara, Gujarat', 'https://images.unsplash.com/photo-1555244162-803834f70033?ixlib=rb-1.2.1&auto=format&fit=crop&w=600&q=80'),

(5, 'Wedding Cake', 'Three-tier custom designed cake with your choice of flavors', 'Catering', 12000.00, 'Surat, Gujarat', 'https://images.unsplash.com/photo-1535141192574-5d4897c12636?ixlib=rb-1.2.1&auto=format&fit=crop&w=600&q=80'),
(5, 'Dessert Bar', 'Assortment of mini desserts and sweet treats', 'Catering', 8000.00, 'Surat, Gujarat', 'https://images.unsplash.com/photo-1535141192574-5d4897c12636?ixlib=rb-1.2.1&auto=format&fit=crop&w=600&q=80'),

-- Photography vendor services
(6, 'Full Day Photography', 'Comprehensive photography coverage for 12 hours', 'Photography', 25000.00, 'Vadodara, Gujarat', 'https://images.unsplash.com/photo-1520854221256-17451cc331bf?ixlib=rb-1.2.1&auto=format&fit=crop&w=600&q=80'),
(6, 'Pre-Wedding Shoot', '4-hour photo session at location of your choice', 'Photography', 15000.00, 'Vadodara, Gujarat', 'https://images.unsplash.com/photo-1520854221256-17451cc331bf?ixlib=rb-1.2.1&auto=format&fit=crop&w=600&q=80'),

-- Decoration vendor services
(8, 'Premium Floral Package', 'Elaborate floral arrangements for venue entrance, stage, and tables', 'Decoration', 18000.00, 'Vadodara, Gujarat', 'https://images.unsplash.com/photo-1478146896981-b47f11bdce5f?ixlib=rb-1.2.1&auto=format&fit=crop&w=600&q=80'),
(8, 'Themed Décor', 'Complete venue transformation based on your chosen theme', 'Decoration', 25000.00, 'Vadodara, Gujarat', 'https://images.unsplash.com/photo-1478146896981-b47f11bdce5f?ixlib=rb-1.2.1&auto=format&fit=crop&w=600&q=80'),

-- Entertainment vendor services
(10, 'DJ Package', '6-hour DJ performance with state-of-the-art sound system', 'Entertainment', 15000.00, 'Mumbai, Maharashtra', 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?ixlib=rb-1.2.1&auto=format&fit=crop&w=600&q=80'),
(10, 'Live Band', '4-hour performance by our versatile cover band', 'Entertainment', 25000.00, 'Mumbai, Maharashtra', 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?ixlib=rb-1.2.1&auto=format&fit=crop&w=600&q=80');

-- Reset the auto-increment counters
ALTER TABLE users AUTO_INCREMENT = 12;
ALTER TABLE vendors AUTO_INCREMENT = 11;
ALTER TABLE vendor_services AUTO_INCREMENT = 15;