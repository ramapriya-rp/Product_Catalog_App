import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Container, Typography, Button, Box } from '@mui/material';
import '../index.css'; // Use index.css for styles

interface Product {
  id: number;
  name: string;
  category: string;
  description: string;
  price: number;
  imageUrl: string;
}

const ProductDetail: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { product } = location.state as { product: Product };

  const handleClose = () => {
    navigate(-1); // Navigate back to the previous page
  };

  return (
    <Container className="styled-container">
      <Box className="styled-card">
        <img src={product.imageUrl} alt={product.name} className="styled-image" />
        <Box className="styled-details">
          <Typography variant="h5" component="div" gutterBottom>
            {product.name}
          </Typography>
          <Typography variant="body2" color="textSecondary" gutterBottom>
            Category: {product.category}
          </Typography>
          <Typography variant="body1" component="p" gutterBottom>
            {product.description}
          </Typography>
          <Typography variant="h6" component="p" color="primary" gutterBottom>
            {product.price} SEK
          </Typography>
        </Box>
      </Box>
      <Box display="flex" justifyContent="center" mt={2}>
        <Button variant="contained" color="primary" onClick={handleClose} className="styled-button">
          Close
        </Button>
      </Box>
    </Container>
  );
};

export default ProductDetail;
