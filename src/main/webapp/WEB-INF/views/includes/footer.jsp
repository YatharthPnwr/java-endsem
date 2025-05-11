</div> <!-- End of main container -->
    
    <!-- Footer -->
    <footer class="bg-dark text-white mt-5 py-5">
        <div class="container">
            <div class="row">
                <div class="col-md-4 mb-4 mb-md-0">
                    <h5 class="mb-3">Eventify</h5>
                    <p class="text-muted">Your one-stop platform for all event planning services. Find and book vendors for any occasion, from weddings to corporate events.</p>
                    <div class="d-flex mt-4">
                        <a href="#" class="text-white me-3"><i class="fab fa-facebook-f"></i></a>
                        <a href="#" class="text-white me-3"><i class="fab fa-twitter"></i></a>
                        <a href="#" class="text-white me-3"><i class="fab fa-instagram"></i></a>
                        <a href="#" class="text-white"><i class="fab fa-linkedin-in"></i></a>
                    </div>
                </div>
                
                <div class="col-md-2 mb-4 mb-md-0">
                    <h5 class="mb-3">Quick Links</h5>
                    <ul class="list-unstyled">
                        <li class="mb-2"><a href="${pageContext.request.contextPath}/" class="text-decoration-none text-muted">Home</a></li>
                        <li class="mb-2"><a href="${pageContext.request.contextPath}/vendors" class="text-decoration-none text-muted">Services</a></li>
                        <li class="mb-2"><a href="#" class="text-decoration-none text-muted">How It Works</a></li>
                        <li class="mb-2"><a href="#" class="text-decoration-none text-muted">About Us</a></li>
                    </ul>
                </div>
                
                <div class="col-md-2 mb-4 mb-md-0">
                    <h5 class="mb-3">Services</h5>
                    <ul class="list-unstyled">
                        <li class="mb-2"><a href="${pageContext.request.contextPath}/vendors?type=VENUE" class="text-decoration-none text-muted">Venues</a></li>
                        <li class="mb-2"><a href="${pageContext.request.contextPath}/vendors?type=CATERING" class="text-decoration-none text-muted">Catering</a></li>
                        <li class="mb-2"><a href="${pageContext.request.contextPath}/vendors?type=PHOTOGRAPHY" class="text-decoration-none text-muted">Photography</a></li>
                        <li class="mb-2"><a href="${pageContext.request.contextPath}/vendors?type=DECOR" class="text-decoration-none text-muted">Decoration</a></li>
                    </ul>
                </div>
                
                <div class="col-md-4">
                    <h5 class="mb-3">Contact Us</h5>
                    <ul class="list-unstyled text-muted">
                        <li class="mb-2"><i class="fas fa-map-marker-alt me-2"></i>123, Vasna road, Vadodara, Gujarat</li>
                        <li class="mb-2"><i class="fas fa-phone me-2"></i>+91 999999999</li>
                        <li class="mb-2"><i class="fas fa-envelope me-2"></i>Eventify@gmail.com</li>
                    </ul>
                    
                    <h5 class="mt-4 mb-3">Subscribe to Newsletter</h5>
                    <div class="input-group">
                        <input type="email" class="form-control" placeholder="Your email" aria-label="Your email">
                        <button class="btn btn-primary" type="button">Subscribe</button>
                    </div>
                </div>
            </div>
            
            <hr class="my-4">
            
            <div class="row">
                <div class="col-md-6 text-center text-md-start">
                    <p class="mb-0">&copy; 2025 Eventify. All rights reserved.</p>
                </div>
                <div class="col-md-6 text-center text-md-end">
                    <a href="#" class="text-decoration-none text-muted me-3">Privacy Policy</a>
                    <a href="#" class="text-decoration-none text-muted me-3">Terms of Service</a>
                    <a href="#" class="text-decoration-none text-muted">Sitemap</a>
                </div>
            </div>
        </div>
    </footer>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Custom JS -->
    <script>
        // Add any custom JavaScript here
        document.addEventListener('DOMContentLoaded', function() {
            // Active link highlighting
            const currentLocation = window.location.pathname;
            const navLinks = document.querySelectorAll('.navbar-nav .nav-link');
            
            navLinks.forEach(link => {
                const linkPath = link.getAttribute('href');
                if (linkPath && currentLocation.includes(linkPath) && linkPath !== '/') {
                    link.classList.add('active');
                } else if (linkPath === '/' && currentLocation === '/') {
                    link.classList.add('active');
                }
            });
        });
    </script>
</body>
</html>